# JVM Architecture Notes — Solution

---

## 1. What is the JDK?

**Q:** What does JDK stand for, and what does it contain?

**A:** JDK stands for **Java Development Kit**. It is the complete package developers install to write, compile, and run Java programs. It contains the Java compiler (`javac`), debugging tools, JavaDoc generator, the JRE (and therefore the JVM), and the standard class libraries.

---

## 2. What is the JRE?

**Q:** What does JRE stand for, and what is its purpose?

**A:** JRE stands for **Java Runtime Environment**. Its purpose is to provide the minimum environment needed to *run* (not develop) Java applications. It contains the JVM, the core class libraries (like `java.lang`, `java.util`), and supporting files — but it does NOT include the Java compiler.

---

## 3. What is the JVM?

**Q:** What does JVM stand for, and what is its primary responsibility?

**A:** JVM stands for **Java Virtual Machine**. Its primary responsibility is to take compiled Java bytecode (`.class` files) and execute it by translating the bytecode into native machine instructions that the host operating system and CPU can understand. The JVM is what makes Java platform-independent.

---

## 4. Nested Relationship

**Q:** Complete the sentence: "The JDK contains the ___, which contains the ___."

**A:** "The JDK contains the **JRE**, which contains the **JVM**."

---

## 5. Write Once, Run Anywhere

**Q:** Java source code compiles to **bytecode** (`.class` files), not machine code. Which component is responsible for translating bytecode into native machine instructions at runtime?

**A:** The **JVM** (Java Virtual Machine). Each platform (Windows, macOS, Linux) has its own JVM implementation, but they all understand the same bytecode format — that's what "Write Once, Run Anywhere" means.

---

## 6. Developer vs End User

**Q:** If you are distributing a Java application to a non-developer end user who just wants to run it, do they need the JDK or the JRE? Why?

**A:** The end user only needs the **JRE** (or a modern JDK, since recent Java versions merged them). They don't need the compiler (`javac`) or development tools — they just need the JVM and runtime libraries to run the pre-compiled `.class` or `.jar` file.

---

## 7. Diagram

**Q:** Draw a simple nested-box diagram (using text/ASCII art) showing how JDK, JRE, and JVM relate to each other.

**A:**
```
+----------------------------------------------+
|                    JDK                       |
|   (Java compiler, javadoc, debugger, tools)  |
|                                              |
|  +----------------------------------------+ |
|  |                  JRE                   | |
|  |  (Class libraries: java.lang, etc.)    | |
|  |                                        | |
|  |  +----------------------------------+  | |
|  |  |             JVM                 |  | |
|  |  | (Executes bytecode → machine    |  | |
|  |  |  instructions for the host OS)  |  | |
|  |  +----------------------------------+  | |
|  +----------------------------------------+ |
+----------------------------------------------+
```
