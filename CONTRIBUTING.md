# Contributing to User Segmentation DSL

Thank you for your interest in contributing to the User Segmentation DSL! This document provides guidelines and instructions for contributing to the project.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Workflow](#development-workflow)
- [Adding New Functions](#adding-new-functions)
- [Testing Guidelines](#testing-guidelines)
- [Documentation](#documentation)
- [Submitting Changes](#submitting-changes)
- [Release Process](#release-process)

## Code of Conduct

This project adheres to a code of conduct that all contributors are expected to follow. Please be respectful and constructive in all interactions.

## Getting Started

### Prerequisites

- Java 11 or higher
- Maven 3.6+
- Git
- A Java IDE (IntelliJ IDEA, Eclipse, or VS Code recommended)

### Setting Up Your Development Environment

1. Fork the repository on GitHub
2. Clone your fork locally:
   ```bash
   git clone https://github.com/YOUR-USERNAME/user-segmentation-dsl.git
   cd user-segmentation-dsl
   ```

3. Add the upstream repository:
   ```bash
   git remote add upstream https://github.com/example/user-segmentation-dsl.git
   ```

4. Build the project:
   ```bash
   mvn clean install
   ```

5. Run the tests to ensure everything works:
   ```bash
   mvn test
   ```

## Development Workflow

1. **Create a branch** for your work:
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Make your changes** following the coding standards

3. **Write tests** for your changes (both unit and property-based tests)

4. **Run the test suite**:
   ```bash
   mvn test
   ```

5. **Update documentation** if needed

6. **Commit your changes** with clear, descriptive commit messages:
   ```bash
   git commit -m "Add: Brief description of your changes"
   ```

7. **Push to your fork**:
   ```bash
   git push origin feature/your-feature-name
   ```

8. **Create a Pull Request** on GitHub

## Adding New Functions

The DSL is designed to be highly extensible. To add a new function:

### 1. Create the Function Class

Create a new Java file in the appropriate package under `src/main/java/com/example/dsl/functions/`:

```java
package com.example.dsl.functions.yourCategory;

import com.example.dsl.functions.DSLFunction;
import com.example.dsl.functions.FunctionMetadata;
import com.googlecode.aviator.runtime.type.AviatorObject;
import java.util.Map;

public class YourNewFunction extends DSLFunction {
    
    @Override
    public String getName() {
        return "YOUR_FUNCTION_NAME";  // Must be UPPERCASE
    }
    
    @Override
    public FunctionMetadata getMetadata() {
        return FunctionMetadata.builder()
            .name("YOUR_FUNCTION_NAME")
            .minArgs(1)
            .maxArgs(2)
            .argumentType(0, ArgumentType.STRING)
            .returnType(ReturnType.BOOLEAN)
            .description("Description of what your function does")
            .build();
    }
    
    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        // Validate arguments
        validateArgCount(args, 1);
        
        // Extract and convert arguments using helper methods
        String value = toString(args[0], env);
        
        // Implement your logic
        boolean result = yourLogic(value);
        
        // Return result wrapped in AviatorObject
        return AviatorBoolean.valueOf(result);
    }
}
```

### 2. Register the Function

Add your function to the `FunctionRegistry` in `src/main/java/com/example/dsl/functions/FunctionRegistry.java`:

```java
public void registerAll(AviatorEvaluatorInstance aviator) {
    // ... existing registrations ...
    register(aviator, new YourNewFunction());
}
```

### 3. Write Tests

Create unit tests in `src/test/java/com/example/dsl/unit/functions/`:

```java
@Test
void testYourNewFunction() {
    String expression = "YOUR_FUNCTION_NAME(\"test\")";
    EvaluationResult result = evaluator.evaluate(expression, userData);
    
    assertTrue(result.isSuccess());
    assertEquals(expectedValue, result.getValue());
}
```

If applicable, add property-based tests in `src/test/java/com/example/dsl/property/`:

```java
@Property
void testYourFunctionProperty(@ForAll String input) {
    // Test universal properties that should hold for all inputs
}
```

### 4. Update Documentation

- Add your function to `docs/FUNCTION_REFERENCE.md`
- Add usage examples to `docs/USE_CASE_EXAMPLES.md` if applicable
- Update the README.md function list

## Testing Guidelines

### Unit Tests

- Write unit tests for specific examples and edge cases
- Test error conditions (invalid inputs, null values, etc.)
- Use descriptive test names that explain what is being tested
- Aim for high code coverage (>80%)

### Property-Based Tests

- Write property tests for universal behaviors
- Test algebraic laws (commutativity, associativity, etc.)
- Test invariants that should always hold
- Use at least 100 iterations per property test

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=YourTestClass

# Run with coverage report
mvn clean test jacoco:report
```

## Documentation

### Code Documentation

- Add Javadoc comments to all public classes and methods
- Include `@param`, `@return`, and `@throws` tags
- Provide usage examples in Javadoc when helpful

### User Documentation

When adding new features, update:
- `README.md` - Quick start and overview
- `docs/API.md` - Detailed API documentation
- `docs/FUNCTION_REFERENCE.md` - Function catalog
- `docs/USE_CASE_EXAMPLES.md` - Practical examples
- `CHANGELOG.md` - Record your changes

## Submitting Changes

### Pull Request Guidelines

1. **Title**: Use a clear, descriptive title
   - `Add: New function for X`
   - `Fix: Issue with Y function`
   - `Docs: Update Z documentation`

2. **Description**: Include:
   - What changes you made and why
   - Any related issue numbers
   - Testing performed
   - Documentation updates

3. **Checklist**:
   - [ ] Code follows project style guidelines
   - [ ] Tests added/updated and passing
   - [ ] Documentation updated
   - [ ] CHANGELOG.md updated
   - [ ] No breaking changes (or clearly documented)

### Code Review Process

1. Maintainers will review your PR
2. Address any feedback or requested changes
3. Once approved, your PR will be merged

## Release Process

### Versioning

This project follows [Semantic Versioning](https://semver.org/):

- **MAJOR** (X.0.0): Incompatible API changes
- **MINOR** (x.Y.0): Backwards-compatible new features
- **PATCH** (x.y.Z): Backwards-compatible bug fixes

### Creating a Release

1. Update version in `pom.xml`
2. Update `CHANGELOG.md` with release date
3. Create a git tag: `git tag -a v1.0.0 -m "Release version 1.0.0"`
4. Push tag: `git push origin v1.0.0`
5. Deploy to Maven repository: `mvn clean deploy`
6. Create GitHub release with release notes

## Questions?

If you have questions or need help:

- Open an issue on GitHub
- Check existing documentation in the `docs/` folder
- Review the extension guide: `docs/EXTENSION_GUIDE.md`

Thank you for contributing to User Segmentation DSL!
