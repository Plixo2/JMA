

<div align="center">

<h3 align="center">:mag:JVM Model Api</h3>

</div>

<br>

![Test Status](https://github.com/Plixo2/JMA/actions/workflows/gradle.yml/badge.svg)
![Java Version](https://img.shields.io/badge/Java-21-orange) 
[![License: MIT/Apache-2.0](https://img.shields.io/badge/License-Apache--2.0%20%7C%20MIT-blue)](https://opensource.org/licenses/MIT)
![Windows](https://img.shields.io/badge/Windows-0078D6?style=flat)
![Linux](https://img.shields.io/badge/Linux-FCC624?style=flat&logo=linux&logoColor=black)
![MacOS](https://img.shields.io/badge/MacOS-000000?style=flat&logo=apple&logoColor=white)




**JMA (JVM Model API)** is a high-level framework for analyzing, validating, and generating JVM class structures, supporting class files from **Java 8 to Java 24**.

It is designed to be:
- 🔒 **Fully compatible** with the [_Java® Virtual Machine Specification, Java SE 24 Edition_](https://docs.oracle.com/javase/specs/jvms/se24/html/index.html)
- 🔁 **Interoperable** with tools like [ASM](https://asm.ow2.io/)
- 🔧 **Extensible** with custom validation, linking, and type resolution mechanisms
- 🧵 **Modular** with full support for the **Java Platform Module System (JPMS)**
- 🧠 **Compiler-oriented**, aiming to serve as a backend framework for **language implementations** on the JVM

JMA handles generics, annotations, bridge method generation, module resolution, and provides **meaningful error messages** — including **source code locations** — to simplify debugging and diagnostics.

It is ideal for:
- Programmatic class file manipulation
- Building compilers for new JVM languages
- Validating or rewriting `.class` or `.jar` files with full spec compliance

> ✅ See [Karina](https://karina-lang.org/) for an example of a JVM language built on top of JMA.

---

## 🚀 Quick Example

```java
// Locate the JDK's java.base.jmod file (which contains the core Java classes)
var javaHome = Objects.requireNonNull(System.getProperty("java.home"));
if (javaHome.endsWith("jre")) { javaHome = Paths.get(javaHome).getParent().toString(); }
var jmodPath = Paths.get(javaHome, "jmods", "java.base.jmod");

// Load, link and verify the core classes
UnlinkedModel jdkUnlinked = ModelReader.fromJMod(jmodPath);
Model jdkModel = ModelLinker.DEFAULT.link(jdkUnlinked);
ModelVerifier.DEFAULT.verify(jdkModel);

// Create a linker to reference the JDK model
var jdkLinker = new ModelLinker(jdkModel);
// Create a verifier to reference the JDK model
var jdkVerifier = new ModelVerifier(jdkModel);

// Create a custom class via ASM
var node = new ClassNode();
node.name = "com/example/customClass";
node.version = Flags.VERSION_LATEST;
node.superName = "java/lang/Object";
node.access = Flags.PUBLIC;

var identifier = ClassIdentifier.of("CustomClass");
// Convert into an UnlinkedClass
UnlinkedClass customUnlinked = ClassNodeParser.parse(identifier, node);
// Create a Model with a single class
UnlinkedModel customModel = UnlinkedModel.of(customUnlinked);
// Link against the existing core classes
Model linkedModel = jdkLinker.link(customModel);
jdkVerifier.verify(linkedModel);
```

---