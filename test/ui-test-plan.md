# UI Test Plan

These tests run the chatbot as a console program. Each test case includes the
user name as the first input line because the program asks for the name before
accepting commands.

Deadline behavior is split across these cases:

- `deadline` with no description is rejected with the expected format.
- `deadline /by <deadline>` with no description is rejected with the expected format.
- `deadline <description>` without `/by` is rejected with the expected format.
- `deadline <description> /by <deadline>` creates a deadline task.

Event behavior is split across these cases:

- `event` with no description is rejected with the expected format.
- `event /from <start> /to <end>` with no description is rejected with the expected format.
- `event <description> /to <end>` without `/from` is rejected with the expected format.
- `event <description> /from <start>` without `/to` is rejected with the expected format.
- `event <description> /from <start> /to <end>` creates an event task.

General command behavior:

- `todo` with no description is rejected with the expected format.
- Unknown commands ask the user to type `help`.
- `help` lists all accepted inputs and their formats.

### TC-001 Todo Command Without Description

Aim: Verify that `todo` without a description is rejected with the expected format.

Inputs:

```text
Alex
todo
bye
```

Expected output:

```text
____________________________________________________________
                       _           _   _           _   
 _ __ ___  _ __    ___| |__   __ _| |_| |__   ___ | |_ 
| '_ ` _ \| '__|  / __| '_ \ / _` | __| '_ \ / _ \| __|
| | | | | | |    | (__| | | | (_| | |_| |_) | (_) | |_ 
|_| |_| |_|_|     \___|_| |_|\__,_|\__|_.__/ \___/ \__|

Hello! I'm Mr Chatbot, your personal companion.
What is your name?
What can I do for you, Mr Alex?
____________________________________________________________
____________________________________________________________
Please use the format: todo <description>
____________________________________________________________
____________________________________________________________
Bye Mr Alex. Hope to see you again soon!
____________________________________________________________
```

### TC-002 Bare Deadline Command Without Description

Aim: Verify that bare `deadline` without a task description is rejected with the expected format.

Inputs:

```text
Alex
deadline
bye
```

Expected output:

```text
____________________________________________________________
                       _           _   _           _   
 _ __ ___  _ __    ___| |__   __ _| |_| |__   ___ | |_ 
| '_ ` _ \| '__|  / __| '_ \ / _` | __| '_ \ / _ \| __|
| | | | | | |    | (__| | | | (_| | |_| |_) | (_) | |_ 
|_| |_| |_|_|     \___|_| |_|\__,_|\__|_.__/ \___/ \__|

Hello! I'm Mr Chatbot, your personal companion.
What is your name?
What can I do for you, Mr Alex?
____________________________________________________________
____________________________________________________________
Please use the format: deadline <description> /by <deadline>
____________________________________________________________
____________________________________________________________
Bye Mr Alex. Hope to see you again soon!
____________________________________________________________
```

### TC-003 Bare Event Command Without Description Or Times

Aim: Verify that bare `event` without a description, `/from`, and `/to` values is rejected with the expected format.

Inputs:

```text
Alex
event
bye
```

Expected output:

```text
____________________________________________________________
                       _           _   _           _   
 _ __ ___  _ __    ___| |__   __ _| |_| |__   ___ | |_ 
| '_ ` _ \| '__|  / __| '_ \ / _` | __| '_ \ / _ \| __|
| | | | | | |    | (__| | | | (_| | |_| |_) | (_) | |_ 
|_| |_| |_|_|     \___|_| |_|\__,_|\__|_.__/ \___/ \__|

Hello! I'm Mr Chatbot, your personal companion.
What is your name?
What can I do for you, Mr Alex?
____________________________________________________________
____________________________________________________________
Please use the format: event <description> /from <start> /to <end>
____________________________________________________________
____________________________________________________________
Bye Mr Alex. Hope to see you again soon!
____________________________________________________________
```

### TC-004 Deadline Command With By But Without Description

Aim: Verify that `deadline /by <deadline>` without a task description is rejected with the expected format.

Inputs:

```text
Alex
deadline /by sunday
bye
```

Expected output:

```text
____________________________________________________________
                       _           _   _           _   
 _ __ ___  _ __    ___| |__   __ _| |_| |__   ___ | |_ 
| '_ ` _ \| '__|  / __| '_ \ / _` | __| '_ \ / _ \| __|
| | | | | | |    | (__| | | | (_| | |_| |_) | (_) | |_ 
|_| |_| |_|_|     \___|_| |_|\__,_|\__|_.__/ \___/ \__|

Hello! I'm Mr Chatbot, your personal companion.
What is your name?
What can I do for you, Mr Alex?
____________________________________________________________
____________________________________________________________
Please use the format: deadline <description> /by <deadline>
____________________________________________________________
____________________________________________________________
Bye Mr Alex. Hope to see you again soon!
____________________________________________________________
```

### TC-005 Deadline Command With Description But Without By Argument

Aim: Verify that `deadline <description>` without `/by` is rejected with the expected format.

Inputs:

```text
Alex
deadline return book
bye
```

Expected output:

```text
____________________________________________________________
                       _           _   _           _   
 _ __ ___  _ __    ___| |__   __ _| |_| |__   ___ | |_ 
| '_ ` _ \| '__|  / __| '_ \ / _` | __| '_ \ / _ \| __|
| | | | | | |    | (__| | | | (_| | |_| |_) | (_) | |_ 
|_| |_| |_|_|     \___|_| |_|\__,_|\__|_.__/ \___/ \__|

Hello! I'm Mr Chatbot, your personal companion.
What is your name?
What can I do for you, Mr Alex?
____________________________________________________________
____________________________________________________________
Please use the format: deadline <description> /by <deadline>
____________________________________________________________
____________________________________________________________
Bye Mr Alex. Hope to see you again soon!
____________________________________________________________
```

### TC-006 Deadline Command With Description And By Argument

Aim: Verify that `deadline <description> /by <deadline>` creates a deadline task.

Inputs:

```text
Alex
deadline return book /by Sunday
bye
```

Expected output:

```text
____________________________________________________________
                       _           _   _           _   
 _ __ ___  _ __    ___| |__   __ _| |_| |__   ___ | |_ 
| '_ ` _ \| '__|  / __| '_ \ / _` | __| '_ \ / _ \| __|
| | | | | | |    | (__| | | | (_| | |_| |_) | (_) | |_ 
|_| |_| |_|_|     \___|_| |_|\__,_|\__|_.__/ \___/ \__|

Hello! I'm Mr Chatbot, your personal companion.
What is your name?
What can I do for you, Mr Alex?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Sunday)
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Bye Mr Alex. Hope to see you again soon!
____________________________________________________________
```

### TC-007 Event Command With From And To But Without Description

Aim: Verify that `event /from <start> /to <end>` without a description is rejected with the expected format.

Inputs:

```text
Alex
event /from Mon 2pm /to 4pm
bye
```

Expected output:

```text
____________________________________________________________
                       _           _   _           _   
 _ __ ___  _ __    ___| |__   __ _| |_| |__   ___ | |_ 
| '_ ` _ \| '__|  / __| '_ \ / _` | __| '_ \ / _ \| __|
| | | | | | |    | (__| | | | (_| | |_| |_) | (_) | |_ 
|_| |_| |_|_|     \___|_| |_|\__,_|\__|_.__/ \___/ \__|

Hello! I'm Mr Chatbot, your personal companion.
What is your name?
What can I do for you, Mr Alex?
____________________________________________________________
____________________________________________________________
Please use the format: event <description> /from <start> /to <end>
____________________________________________________________
____________________________________________________________
Bye Mr Alex. Hope to see you again soon!
____________________________________________________________
```

### TC-008 Event Command Without From Argument

Aim: Verify that `event <description> /to <end>` without `/from` is rejected with the expected format.

Inputs:

```text
Alex
event meeting /to 4pm
bye
```

Expected output:

```text
____________________________________________________________
                       _           _   _           _   
 _ __ ___  _ __    ___| |__   __ _| |_| |__   ___ | |_ 
| '_ ` _ \| '__|  / __| '_ \ / _` | __| '_ \ / _ \| __|
| | | | | | |    | (__| | | | (_| | |_| |_) | (_) | |_ 
|_| |_| |_|_|     \___|_| |_|\__,_|\__|_.__/ \___/ \__|

Hello! I'm Mr Chatbot, your personal companion.
What is your name?
What can I do for you, Mr Alex?
____________________________________________________________
____________________________________________________________
Please use the format: event <description> /from <start> /to <end>
____________________________________________________________
____________________________________________________________
Bye Mr Alex. Hope to see you again soon!
____________________________________________________________
```

### TC-009 Event Command Without To Argument

Aim: Verify that `event <description> /from <start>` without `/to` is rejected with the expected format.

Inputs:

```text
Alex
event meeting /from Mon 2pm
bye
```

Expected output:

```text
____________________________________________________________
                       _           _   _           _   
 _ __ ___  _ __    ___| |__   __ _| |_| |__   ___ | |_ 
| '_ ` _ \| '__|  / __| '_ \ / _` | __| '_ \ / _ \| __|
| | | | | | |    | (__| | | | (_| | |_| |_) | (_) | |_ 
|_| |_| |_|_|     \___|_| |_|\__,_|\__|_.__/ \___/ \__|

Hello! I'm Mr Chatbot, your personal companion.
What is your name?
What can I do for you, Mr Alex?
____________________________________________________________
____________________________________________________________
Please use the format: event <description> /from <start> /to <end>
____________________________________________________________
____________________________________________________________
Bye Mr Alex. Hope to see you again soon!
____________________________________________________________
```

### TC-010 Event Command With Description, From, And To Arguments

Aim: Verify that `event <description> /from <start> /to <end>` creates an event task.

Inputs:

```text
Alex
event meeting /from Mon 2pm /to 4pm
bye
```

Expected output:

```text
____________________________________________________________
                       _           _   _           _   
 _ __ ___  _ __    ___| |__   __ _| |_| |__   ___ | |_ 
| '_ ` _ \| '__|  / __| '_ \ / _` | __| '_ \ / _ \| __|
| | | | | | |    | (__| | | | (_| | |_| |_) | (_) | |_ 
|_| |_| |_|_|     \___|_| |_|\__,_|\__|_.__/ \___/ \__|

Hello! I'm Mr Chatbot, your personal companion.
What is your name?
What can I do for you, Mr Alex?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] meeting (from: Mon 2pm to: 4pm)
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Bye Mr Alex. Hope to see you again soon!
____________________________________________________________
```

### TC-011 Unknown Command

Aim: Verify that a command that does not match any accepted input asks the user to type `help`.

Inputs:

```text
Alex
blah
bye
```

Expected output:

```text
____________________________________________________________
                       _           _   _           _   
 _ __ ___  _ __    ___| |__   __ _| |_| |__   ___ | |_ 
| '_ ` _ \| '__|  / __| '_ \ / _` | __| '_ \ / _ \| __|
| | | | | | |    | (__| | | | (_| | |_| |_) | (_) | |_ 
|_| |_| |_|_|     \___|_| |_|\__,_|\__|_.__/ \___/ \__|

Hello! I'm Mr Chatbot, your personal companion.
What is your name?
What can I do for you, Mr Alex?
____________________________________________________________
____________________________________________________________
Sorry, I don't understand that command. Please type "help".
____________________________________________________________
____________________________________________________________
Bye Mr Alex. Hope to see you again soon!
____________________________________________________________
```

### TC-012 Help Command

Aim: Verify that `help` lists all accepted inputs and their formats.

Inputs:

```text
Alex
help
bye
```

Expected output:

```text
____________________________________________________________
                       _           _   _           _   
 _ __ ___  _ __    ___| |__   __ _| |_| |__   ___ | |_ 
| '_ ` _ \| '__|  / __| '_ \ / _` | __| '_ \ / _ \| __|
| | | | | | |    | (__| | | | (_| | |_| |_) | (_) | |_ 
|_| |_| |_|_|     \___|_| |_|\__,_|\__|_.__/ \___/ \__|

Hello! I'm Mr Chatbot, your personal companion.
What is your name?
What can I do for you, Mr Alex?
____________________________________________________________
____________________________________________________________
Accepted inputs:
todo <description>
deadline <description> /by <deadline>
event <description> /from <start> /to <end>
list
mark <task number>
unmark <task number>
bye
help
____________________________________________________________
____________________________________________________________
Bye Mr Alex. Hope to see you again soon!
____________________________________________________________
```

### TC-013 Mark Command Without Task Number

Aim: Verify that `mark` without a task number is rejected.

Inputs:

```text
Alex
mark
bye
```

Expected output:

```text
____________________________________________________________
                       _           _   _           _   
 _ __ ___  _ __    ___| |__   __ _| |_| |__   ___ | |_ 
| '_ ` _ \| '__|  / __| '_ \ / _` | __| '_ \ / _ \| __|
| | | | | | |    | (__| | | | (_| | |_| |_) | (_) | |_ 
|_| |_| |_|_|     \___|_| |_|\__,_|\__|_.__/ \___/ \__|

Hello! I'm Mr Chatbot, your personal companion.
What is your name?
What can I do for you, Mr Alex?
____________________________________________________________
____________________________________________________________
Sorry, I don't understand that task format.
____________________________________________________________
____________________________________________________________
Bye Mr Alex. Hope to see you again soon!
____________________________________________________________
```

### TC-014 Unmark Command Without Task Number

Aim: Verify that `unmark` without a task number is rejected.

Inputs:

```text
Alex
unmark
bye
```

Expected output:

```text
____________________________________________________________
                       _           _   _           _   
 _ __ ___  _ __    ___| |__   __ _| |_| |__   ___ | |_ 
| '_ ` _ \| '__|  / __| '_ \ / _` | __| '_ \ / _ \| __|
| | | | | | |    | (__| | | | (_| | |_| |_) | (_) | |_ 
|_| |_| |_|_|     \___|_| |_|\__,_|\__|_.__/ \___/ \__|

Hello! I'm Mr Chatbot, your personal companion.
What is your name?
What can I do for you, Mr Alex?
____________________________________________________________
____________________________________________________________
Sorry, I don't understand that task format.
____________________________________________________________
____________________________________________________________
Bye Mr Alex. Hope to see you again soon!
____________________________________________________________
```
