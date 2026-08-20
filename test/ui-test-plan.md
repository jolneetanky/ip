# UI Test Plan

These tests run the chatbot as a console program.

Deadline behavior is split across these cases:

- `deadline` with no description and no `/by` is rejected with a message naming both missing parts.
- `deadline /by <deadline>` with no description is rejected with a description-specific message.
- `deadline <description>` without `/by` is rejected with a `/by`-specific message.
- `deadline <description> /by` with no deadline is rejected with a `/by`-specific message.
- `deadline <description> /by <deadline>` creates a deadline task.

Event behavior is split across these cases:

- `event` with no description, `/from`, and `/to` is rejected with a message naming all three missing parts.
- `event /from <start> /to <end>` with no description is rejected with a description-specific message.
- `event <description> /to <end>` without `/from` is rejected with a `/from`-specific message.
- `event <description> /from <start>` without `/to` is rejected with a `/to`-specific message.
- `event <description>` without `/from` and `/to` is rejected with a message naming both missing parts.
- `event /to <end>` without description and `/from` is rejected with a message naming both missing parts.
- `event /from <start>` without description and `/to` is rejected with a message naming both missing parts.
- `event /from /to <end>` without description and a `/from` value is rejected with a message naming both missing parts.
- `event <description> /from <start> /to <end>` creates an event task.
- `event <description> /to <end> /from <start>` creates an event task even when `/to` comes before `/from`.

General command behavior:

- `todo` with no description is rejected with a description-specific error.
- Unknown commands ask the user to type `help`.
- `help` lists all accepted inputs and their formats.

### TC-001 Todo Command Without Description

Aim: Verify that `todo` without a description is rejected with a description-specific error.

Inputs:

```text
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
What can I do for you, Mr User?
____________________________________________________________
____________________________________________________________
Todo description cannot be empty. Please use the format: todo <description>
____________________________________________________________
____________________________________________________________
Bye Mr User. Hope to see you again soon!
____________________________________________________________
```

### TC-002 Bare Deadline Command Without Description

Aim: Verify that bare `deadline` without a task description and `/by` is rejected with a message naming both missing parts.

Inputs:

```text
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
What can I do for you, Mr User?
____________________________________________________________
____________________________________________________________
Deadline description and /by cannot be empty. Please use the format: deadline <description> /by <deadline>
____________________________________________________________
____________________________________________________________
Bye Mr User. Hope to see you again soon!
____________________________________________________________
```

### TC-003 Bare Event Command Without Description Or Times

Aim: Verify that bare `event` without a description, `/from`, and `/to` values is rejected with a message naming all three missing parts.

Inputs:

```text
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
What can I do for you, Mr User?
____________________________________________________________
____________________________________________________________
Event description, /from, and /to cannot be empty. Please use the format: event <description> /from <start> /to <end>
____________________________________________________________
____________________________________________________________
Bye Mr User. Hope to see you again soon!
____________________________________________________________
```

### TC-004 Deadline Command With By But Without Description

Aim: Verify that `deadline /by <deadline>` without a task description is rejected with a description-specific message.

Inputs:

```text
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
What can I do for you, Mr User?
____________________________________________________________
____________________________________________________________
Deadline description cannot be empty. Please use the format: deadline <description> /by <deadline>
____________________________________________________________
____________________________________________________________
Bye Mr User. Hope to see you again soon!
____________________________________________________________
```

### TC-005 Deadline Command With Description But Without By Argument

Aim: Verify that `deadline <description>` without `/by` is rejected with a `/by`-specific message.

Inputs:

```text
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
What can I do for you, Mr User?
____________________________________________________________
____________________________________________________________
Deadline /by cannot be empty. Please use the format: deadline <description> /by <deadline>
____________________________________________________________
____________________________________________________________
Bye Mr User. Hope to see you again soon!
____________________________________________________________
```

### TC-006 Deadline Command With Description And Blank By Argument

Aim: Verify that `deadline <description> /by` without a deadline is rejected with a `/by`-specific message.

Inputs:

```text
deadline return book /by 
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
What can I do for you, Mr User?
____________________________________________________________
____________________________________________________________
Deadline /by cannot be empty. Please use the format: deadline <description> /by <deadline>
____________________________________________________________
____________________________________________________________
Bye Mr User. Hope to see you again soon!
____________________________________________________________
```

### TC-007 Deadline Command With Description And By Argument

Aim: Verify that `deadline <description> /by <deadline>` creates a deadline task.

Inputs:

```text
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
What can I do for you, Mr User?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Sunday)
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Bye Mr User. Hope to see you again soon!
____________________________________________________________
```

### TC-008 Event Command With From And To But Without Description

Aim: Verify that `event /from <start> /to <end>` without a description is rejected with a description-specific message.

Inputs:

```text
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
What can I do for you, Mr User?
____________________________________________________________
____________________________________________________________
Event description cannot be empty. Please use the format: event <description> /from <start> /to <end>
____________________________________________________________
____________________________________________________________
Bye Mr User. Hope to see you again soon!
____________________________________________________________
```

### TC-009 Event Command Without From Argument

Aim: Verify that `event <description> /to <end>` without `/from` is rejected with a `/from`-specific message.

Inputs:

```text
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
What can I do for you, Mr User?
____________________________________________________________
____________________________________________________________
Event /from cannot be empty. Please use the format: event <description> /from <start> /to <end>
____________________________________________________________
____________________________________________________________
Bye Mr User. Hope to see you again soon!
____________________________________________________________
```

### TC-010 Event Command Without To Argument

Aim: Verify that `event <description> /from <start>` without `/to` is rejected with a `/to`-specific message.

Inputs:

```text
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
What can I do for you, Mr User?
____________________________________________________________
____________________________________________________________
Event /to cannot be empty. Please use the format: event <description> /from <start> /to <end>
____________________________________________________________
____________________________________________________________
Bye Mr User. Hope to see you again soon!
____________________________________________________________
```

### TC-011 Event Command With Description But Without From And To Arguments

Aim: Verify that `event <description>` without `/from` and `/to` is rejected with a message naming both missing parts.

Inputs:

```text
event meeting
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
What can I do for you, Mr User?
____________________________________________________________
____________________________________________________________
Event /from and /to cannot be empty. Please use the format: event <description> /from <start> /to <end>
____________________________________________________________
____________________________________________________________
Bye Mr User. Hope to see you again soon!
____________________________________________________________
```

### TC-012 Event Command With To But Without Description And From

Aim: Verify that `event /to <end>` without description and `/from` is rejected with a message naming both missing parts.

Inputs:

```text
event /to 4pm
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
What can I do for you, Mr User?
____________________________________________________________
____________________________________________________________
Event description and /from cannot be empty. Please use the format: event <description> /from <start> /to <end>
____________________________________________________________
____________________________________________________________
Bye Mr User. Hope to see you again soon!
____________________________________________________________
```

### TC-013 Event Command With From But Without Description And To

Aim: Verify that `event /from <start>` without description and `/to` is rejected with a message naming both missing parts.

Inputs:

```text
event /from Mon 2pm
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
What can I do for you, Mr User?
____________________________________________________________
____________________________________________________________
Event description and /to cannot be empty. Please use the format: event <description> /from <start> /to <end>
____________________________________________________________
____________________________________________________________
Bye Mr User. Hope to see you again soon!
____________________________________________________________
```

### TC-014 Event Command With Blank From And To Argument

Aim: Verify that `event /from /to <end>` without description and a `/from` value is rejected with a message naming both missing parts.

Inputs:

```text
event /from /to 3
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
What can I do for you, Mr User?
____________________________________________________________
____________________________________________________________
Event description and /from cannot be empty. Please use the format: event <description> /from <start> /to <end>
____________________________________________________________
____________________________________________________________
Bye Mr User. Hope to see you again soon!
____________________________________________________________
```

### TC-015 Event Command With Description, From, And To Arguments

Aim: Verify that `event <description> /from <start> /to <end>` creates an event task.

Inputs:

```text
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
What can I do for you, Mr User?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] meeting (from: Mon 2pm to: 4pm)
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Bye Mr User. Hope to see you again soon!
____________________________________________________________
```

### TC-016 Event Command With Description, To, And From Arguments

Aim: Verify that `event <description> /to <end> /from <start>` creates an event task when `/to` comes before `/from`.

Inputs:

```text
event meeting /to 4pm /from Mon 2pm
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
What can I do for you, Mr User?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] meeting (from: Mon 2pm to: 4pm)
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Bye Mr User. Hope to see you again soon!
____________________________________________________________
```

### TC-017 Unknown Command

Aim: Verify that a command that does not match any accepted input asks the user to type `help`.

Inputs:

```text
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
What can I do for you, Mr User?
____________________________________________________________
____________________________________________________________
Sorry, I don't understand that command. Please type "help".
____________________________________________________________
____________________________________________________________
Bye Mr User. Hope to see you again soon!
____________________________________________________________
```

### TC-018 Help Command

Aim: Verify that `help` lists all accepted inputs and their formats.

Inputs:

```text
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
What can I do for you, Mr User?
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
Bye Mr User. Hope to see you again soon!
____________________________________________________________
```

### TC-019 Mark Command Without Task Number

Aim: Verify that `mark` without a task number is rejected.

Inputs:

```text
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
What can I do for you, Mr User?
____________________________________________________________
____________________________________________________________
Sorry, I don't understand that task format.
____________________________________________________________
____________________________________________________________
Bye Mr User. Hope to see you again soon!
____________________________________________________________
```

### TC-020 Unmark Command Without Task Number

Aim: Verify that `unmark` without a task number is rejected.

Inputs:

```text
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
What can I do for you, Mr User?
____________________________________________________________
____________________________________________________________
Sorry, I don't understand that task format.
____________________________________________________________
____________________________________________________________
Bye Mr User. Hope to see you again soon!
____________________________________________________________
```
