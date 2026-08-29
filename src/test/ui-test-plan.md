# Console UI Test Plan

## How to run

Use Java 25. Compile the application into a temporary directory, then run the
`Sandrone` class. A typical test command is:

```bash
TEST_UI_JAVA_HOME=$(/usr/libexec/java_home -v 25)
PROJECT_ROOT=$(pwd)
mkdir -p _temp/ui-test-classes
"$TEST_UI_JAVA_HOME/bin/javac" -d _temp/ui-test-classes $(find src/main/java -name '*.java')
TEST_DIR=$(mktemp -d)
(cd "$TEST_DIR" && "$TEST_UI_JAVA_HOME/bin/java" -cp "$PROJECT_ROOT/_temp/ui-test-classes" sandrone.Sandrone)
```

Run every test case in a fresh process. Compare its complete output exactly,
including spaces and blank lines. Use a separate empty `TEST_DIR` for each test
case so that saved task data does not affect another test.

## Test cases

### TC-02: Manage and display every task type

**Aim:** Verify that the user can add todo, deadline, and event tasks; mark and
unmark a task; and list all saved-in-memory tasks.

**Inputs:**

```text
todo read book
deadline return book /by 29/8/2026 1430
event project meeting /from 30/8/2026 1400 /to 30/8/2026 1600
mark 1
unmark 1
list
bye
```

**Expected output:**

```text
____________________________________________________________
 SSSS    A   N   N DDDD  RRRR   OOO  N   N EEEEE
S       A A  NN  N D   D R   R O   O NN  N E    
 SSS   AAAAA N N N D   D RRRR  O   O N N N EEEE 
    S  A   A N  NN D   D R R   O   O N  NN E    
SSSS   A   A N   N DDDD  R  RR  OOO  N   N EEEEE

Tch... Hello. I'm Sandrone. ...Don't make me say it again.
What do you want?
____________________________________________________________

____________________________________________________________
 added: todo read book
You now have 1 tasks in the list
____________________________________________________________

____________________________________________________________
 added: deadline return book /by 29/8/2026 1430
You now have 2 tasks in the list
____________________________________________________________

____________________________________________________________
 added: event project meeting /from 30/8/2026 1400 /to 30/8/2026 1600
You now have 3 tasks in the list
____________________________________________________________

____________________________________________________________
 Nice! I've marked this task as done:
   [X] read book
____________________________________________________________

____________________________________________________________
 OK, I've marked this task as not done yet:
   [ ] read book
____________________________________________________________

____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[D][ ] return book (by: 29/8/2026 2:30PM)
 3.[E][ ] project meeting (from: 30/8/2026 2:00PM to: 30/8/2026 4:00PM)
____________________________________________________________

____________________________________________________________
Bye...
____________________________________________________________

```

### TC-03: Load tasks saved by an earlier session

**Aim:** Verify that a fresh chatbot process restores todo, deadline, and event
tasks, including their completion status, from `data/tasks.txt`.

**Setup:** Before starting the tested chatbot process, ensure that
`TEST_DIR/data/tasks.txt` already contains the following saved tasks. One way
to create this file is to run the listed preliminary session in the same
`TEST_DIR`:

```text
T | 0 | read book
D | 1 | return book | 29/8/2026 2:30PM
E | 0 | project meeting | 30/8/2026 2:00PM | 30/8/2026 4:00PM
```

Preliminary session to create that file through the application:

```text
todo read book
deadline return book /by 29/8/2026 1430
event project meeting /from 30/8/2026 1400 /to 30/8/2026 1600
mark 2
bye
```

**Inputs:**

```text
list
bye
```

**Expected output:**

```text
____________________________________________________________
 SSSS    A   N   N DDDD  RRRR   OOO  N   N EEEEE
S       A A  NN  N D   D R   R O   O NN  N E    
 SSS   AAAAA N N N D   D RRRR  O   O N N N EEEE 
    S  A   A N  NN D   D R R   O   O N  NN E    
SSSS   A   A N   N DDDD  R  RR  OOO  N   N EEEEE

Tch... Hello. I'm Sandrone. ...Don't make me say it again.
What do you want?
____________________________________________________________

____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[D][X] return book (by: 29/8/2026 2:30PM)
 3.[E][ ] project meeting (from: 30/8/2026 2:00PM to: 30/8/2026 4:00PM)
____________________________________________________________

____________________________________________________________
Bye...
____________________________________________________________

```

### TC-01: Exit after the welcome message

**Aim:** Verify that the application welcomes the user and exits politely when
the user enters `bye`.

**Inputs:**

```text
bye
```

**Expected output:**

```text
____________________________________________________________
 SSSS    A   N   N DDDD  RRRR   OOO  N   N EEEEE
S       A A  NN  N D   D R   R O   O NN  N E    
 SSS   AAAAA N N N D   D RRRR  O   O N N N EEEE 
    S  A   A N  NN D   D R R   O   O N  NN E    
SSSS   A   A N   N DDDD  R  RR  OOO  N   N EEEEE

Tch... Hello. I'm Sandrone. ...Don't make me say it again.
What do you want?
____________________________________________________________

____________________________________________________________
Bye...
____________________________________________________________

```

### TC-04: Reject malformed commands safely

**Aim:** Verify that incomplete task details, invalid task numbers, unsupported
save-format characters, and unknown commands show an error without terminating
the chatbot.

**Inputs:**

```text
todo
deadline submit report
event discussion /from tomorrow
mark banana
unmark 0
remove 2
todo wrong | data
nonsense
bye
```

**Expected output:**

```text
____________________________________________________________
 SSSS    A   N   N DDDD  RRRR   OOO  N   N EEEEE
S       A A  NN  N D   D R   R O   O NN  N E    
 SSS   AAAAA N N N D   D RRRR  O   O N N N EEEE 
    S  A   A N  NN D   D R R   O   O N  NN E    
SSSS   A   A N   N DDDD  R  RR  OOO  N   N EEEEE

Tch... Hello. I'm Sandrone. ...Don't make me say it again.
What do you want?
____________________________________________________________

____________________________________________________________
Oops! Description cannot be empty
____________________________________________________________

____________________________________________________________
Oops! Deadline must include /by followed by a time
____________________________________________________________

____________________________________________________________
Oops! Event must include /from and /to times
____________________________________________________________

____________________________________________________________
Oops! Task number must be a positive whole number
____________________________________________________________

____________________________________________________________
Oops! Invalid task index
____________________________________________________________

____________________________________________________________
Oops! Invalid task index
____________________________________________________________

____________________________________________________________
Oops! Description cannot contain |
____________________________________________________________

____________________________________________________________
Oops! Invalid command
____________________________________________________________

____________________________________________________________
Bye...
____________________________________________________________

```
