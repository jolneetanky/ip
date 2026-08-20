#!/usr/bin/env python3
"""Run console UI tests described in test/ui-test-plan.md."""

from __future__ import annotations

import argparse
import difflib
import re
import subprocess
import sys
import tempfile
from dataclasses import dataclass
from pathlib import Path


@dataclass
class TestCase:
    name: str
    aim: str
    inputs: str
    expected_output: str


CASE_RE = re.compile(r"^###\s+(.+)$", re.MULTILINE)
FENCE_RE = re.compile(r"```(?:text)?\n(.*?)\n```", re.DOTALL)


def extract_after(label: str, section: str) -> str:
    label_index = section.find(label)
    if label_index == -1:
        raise ValueError(f"Missing '{label}'")
    match = FENCE_RE.search(section, label_index)
    if match is None:
        raise ValueError(f"Missing fenced text block after '{label}'")
    block = match.group(1)
    return block if block.endswith("\n") else block + "\n"


def parse_test_plan(plan_path: Path) -> list[TestCase]:
    text = plan_path.read_text()
    matches = list(CASE_RE.finditer(text))
    cases: list[TestCase] = []
    for index, match in enumerate(matches):
        start = match.end()
        end = matches[index + 1].start() if index + 1 < len(matches) else len(text)
        section = text[start:end]
        aim_match = re.search(r"^Aim:\s*(.+)$", section, re.MULTILINE)
        if aim_match is None:
            raise ValueError(f"Missing Aim in test case '{match.group(1)}'")
        cases.append(
            TestCase(
                name=match.group(1).strip(),
                aim=aim_match.group(1).strip(),
                inputs=extract_after("Inputs:", section),
                expected_output=extract_after("Expected output:", section),
            )
        )
    if not cases:
        raise ValueError(f"No test cases found in {plan_path}")
    return cases


def compile_sources(repo: Path, output_dir: Path) -> None:
    source_files = sorted((repo / "src/main/java").glob("*.java"))
    if not source_files:
        raise ValueError("No Java source files found in src/main/java")
    command = ["javac", "-d", str(output_dir), *[str(path) for path in source_files]]
    result = subprocess.run(command, cwd=repo, capture_output=True, text=True)
    if result.returncode != 0:
        print("Compilation failed.", file=sys.stderr)
        print(result.stdout, end="", file=sys.stderr)
        print(result.stderr, end="", file=sys.stderr)
        sys.exit(result.returncode)


def run_program(repo: Path, classes_dir: Path, inputs: str, main_class: str) -> str:
    result = subprocess.run(
        ["java", "-cp", str(classes_dir), main_class],
        cwd=repo,
        input=inputs,
        capture_output=True,
        text=True,
    )
    return result.stdout


def show_session(case: TestCase, actual_output: str) -> None:
    print(f"===== {case.name} =====")
    print(f"Aim: {case.aim}")
    print("----- Console input -----")
    print(case.inputs)
    print("----- Expected output -----")
    print(case.expected_output)
    print("----- Actual output -----")
    print(actual_output)


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--plan", default="test/ui-test-plan.md")
    parser.add_argument("--main-class", default="MrChatbot")
    args = parser.parse_args()

    repo = Path.cwd()
    plan_path = repo / args.plan
    cases = parse_test_plan(plan_path)

    with tempfile.TemporaryDirectory(prefix="test-ui-classes-") as temp_dir:
        classes_dir = Path(temp_dir)
        compile_sources(repo, classes_dir)

        for case in cases:
            actual_output = run_program(repo, classes_dir, case.inputs, args.main_class)
            show_session(case, actual_output)
            if actual_output != case.expected_output:
                print("----- Diff -----")
                diff = difflib.unified_diff(
                    case.expected_output.splitlines(keepends=True),
                    actual_output.splitlines(keepends=True),
                    fromfile="expected",
                    tofile="actual",
                )
                print("".join(diff), end="")
                print(f"FAILED: {case.name}")
                return 1
            print(f"PASSED: {case.name}")

    print(f"All {len(cases)} test case(s) passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
