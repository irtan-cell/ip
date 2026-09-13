# Sandrone

> “Your mind is for having ideas, not holding them.” – David Allen

Sandrone frees your mind from having to remember things you need to do. It is:

- text-based
- easy to learn
- fun to interact with
- super spectacular

And it is ***FREE***! 🕰️

### Getting Started

1. Open the project in your code editor.
2. Configure the project to use JDK 25.
3. Run Sandrone using `./gradlew run`.
4. Add your first task, deadline, or event. For example: `todo Task One`.

If you know Java, here's the main constructor:

```java
public Sandrone(String filePath) {
    ui = new Ui();
    storage = new Storage(Path.of(filePath), ui);
    parser = new Parser();
    tasks = loadTasks(storage.load());
}
```

Example commands:

```text
todo Task One
deadline Send Email /by 2026-08-29 1430
event Meeting /from 2026-08-29 1500 /to 2026-08-29 1700
remove 3
unmark 1
mark 2
list
list 29/8/2026
bye
```

### Features

- [x] Managing tasks
- [ ] Managing deadlines (coming soon)
- [ ] Reminders (coming soon)
