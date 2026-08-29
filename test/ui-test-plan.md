# Console UI Test Plan

## How to run

Use Java 25. Compile the application into a temporary directory, then run the
`Sandrone` class. A typical test command is:

```bash
TEST_UI_JAVA_HOME=$(/usr/libexec/java_home -v 25)
mkdir -p _temp/ui-test-classes
"$TEST_UI_JAVA_HOME/bin/javac" -d _temp/ui-test-classes src/main/java/*.java
"$TEST_UI_JAVA_HOME/bin/java" -cp _temp/ui-test-classes Sandrone
```

Run every test case in a fresh process. Compare its complete output exactly,
including spaces and blank lines.

## Test cases

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
