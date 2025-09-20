# Free Subliminal Text

## Not maintained
This project is no longer maintained. It hasn't been updated in years, so I have put the
source code online, hoping someone will bring it back to life.

[![Download82 Award](http://www.download82.com/images/badges/download82-badge3.png)](http://www.download82.com)

## Summary
Free Subliminal Text (FST), flashes subliminal messages on screen. The default settings use very quick flashes of partially transparent text in random screen locations. Most of the time you shouldn't notice it is running, however, you can make what it does, more or less obvious. FST is still being developed so check back for updates.

![Screenshot](screenshot1.png)

## Feature List
* Completely Free - No hoops to jump through, yours forever.
* Font Control - Font family, size, style, color and transparency are all configurable.
* Text Control - Have each message's words or letters reversed or randomized.
* Placement Control - Left-Top to Right-Bottom, plus random and margin options.
* Message Control - Drag and drop suggestions are available, but any message inside Your Messages can be edited.
* Multiplatform - Windows at work, Mac and/or Linux (think Asus EEE) at home? No problem. FST runs on any platform Java (JRE 1.8+) does.

## Requirements
* Java 1.8+ must be installed.

## Setup
The application FST.jar can be put anywhere. Bear in mind that it will try to create a config file called FSTConfig.xml in the same location.
Configuration

Configuring should be straight forward using the provided config window. Closing of the window will quit FST, so you may want to minimise it when not in use. For those who like XML or want to disable the config window from appearing at start up may want to edit FSTConfig.xml directly.

## Download
* [FST1.5.jar](releases/FST1.5.jar?raw)
* [FST1.42.jar](releases/FST1.42.jar?raw)
* [FST1.41.jar](releases/FST1.41.jar?raw)
* [FST1.40.jar](releases/FST1.40.jar?raw)

## Trouble Shooting

### I can't see anything, I don't think it is working

Hopefully, it is working, but it may be too subtle for your consciousness (or screen). Click the Placement tab and change both x and y locations to "center". Try increasing the timing display. Still can't see it, increase the font alpha (up to 255).

## Notes

### Linux
If FST.jar opens in archive manager instead of executing, right-click and select "Open with Other Application", choose "Use a custom command" and type "java -jar". The window manager should remember for next time.

## New Features drafted for future releases
* More documentation on About Tab
* Add additional built-in messages - Suggestions anyone?

## Release History
### 1.51 - 2025-07-27
* Updated to require Java 1.8.

### 1.50 - 2014-03-01
* Switched to native transparency.
* Now requires Java 1.6.
* Now only saves config on clean exit.
* Fix - message order no longer lost between loads.

### 1.42 - 2013-04-19
* Fix divide by zero error.
* Initial "Test Message" added for first time users.

### 1.41 - 2011-09-01
* Add message order option.
* Fixed user save directory.

### 1.40
* Added minimize on startup option.
* Removed save button - now autosaves.
* Interface tweaks
* Check for updates option - check every 30 days after install.
* Friendly window close dialog with minimize option.
* Fixed paste in message window.
* Now saves valid XML when messages contain special characters.

### 1.3p1
* Added Word and Letter Order options.
* Simplified GUI with a single tab for all options.
* Replaced quit button with enabling window close and adding quit dialog.
* First version uploaded to download sites

### 1.2
* Added GUI

### 1.01
* Bugfix?: Memory Leak? - Now Flushes image data after each flash so we don't keep using up ram whilst waiting for garbage collection.

### 1.0
* 5kb GUI-less program, simply displayed text from xml file.

## Development

This section provides information for developers who want to build, test, and contribute to the FST project.

### Development Setup

#### Required Software
- JDK 8 or higher
- Apache Ant 1.9 or higher
- Git (optional, for version control)

#### IDE Support
The project is set up to work with NetBeans, but can be imported into any Java IDE that supports Ant projects:
- NetBeans: Open the project directly
- Eclipse: Import as Ant project
- IntelliJ IDEA: Import as Ant project

#### Initial Project Setup
1. Clone or download the repository
2. Open a terminal in the project directory
3. Run `ant resolve` to download dependencies

### Build Process

FST uses Apache Ant with Ivy for dependency management. The build system handles compilation, testing, and packaging of the application.

#### Directory Structure
```
fst/
├── src/                  # Source code
├── test/                 # Test code
│   ├── java/             # Test classes
│   └── resources/        # Test resources
├── lib/                  # Ivy-managed dependencies
├── build/                # Compiled classes
├── dist/                 # Distribution files
├── reports/              # Test and coverage reports
├── ivy/                  # Ivy installation
├── ivy.xml               # Dependency definitions
└── build.xml             # Ant build script
```

### Build Targets

FST uses a comprehensive build system with **Ant as Single Source of Truth**. All build logic lives in `build.xml`, ensuring identical behavior between local development and CI/CD environments.

#### Development Workflow Targets

These targets are optimized for fast feedback during development:

- `ant quick-test` - **Fast development cycle** (compile + test)
  - Ideal for rapid iteration during coding
  - Skips slow quality checks for speed

- `ant dev-check` - **Quick development validation** (test + style warnings)
  - Runs tests and style checks without failing the build
  - Perfect for checking your work before committing

- `ant dev` - **Clean development cycle** (clean + compile + test + style)
  - Full clean build with basic quality checks
  - Good for ensuring a clean state

- `ant pre-commit` - **Pre-commit validation** (format + dev-check)
  - Formats code and runs development checks
  - Run this before committing to ensure code quality

#### Quality Assurance Targets

These targets provide comprehensive quality analysis:

- `ant check` - **Complete quality check** (test + coverage + style + bugs, warnings only)
  - Runs all quality tools without failing the build
  - Generates comprehensive reports for review

- `ant ci-check` - **Strict CI checks** (test + coverage + style + bugs, strict mode)
  - Same as `check` but fails on violations
  - Used in CI/CD for quality gates

- `ant ci-pipeline` - **Complete CI/CD pipeline** (clean + ci-check)
  - Full pipeline including clean build
  - Matches exactly what runs in GitHub Actions

- `ant coverage` - **Test coverage analysis**
  - Runs tests with JaCoCo coverage analysis
  - Generates HTML and XML coverage reports

#### Code Quality Targets

Individual quality tools for focused analysis:

- `ant format` - **Format source code** (applies Google Java Format)
- `ant format-check` - **Check code formatting** (without modifying files)
- `ant checkstyle` - **Style check** (strict mode, fails on violations)
- `ant checkstyle-warn` - **Style check** (warning mode, doesn't fail build)
- `ant spotbugs` - **Static analysis** (strict mode, fails on violations)
- `ant spotbugs-warn` - **Static analysis** (warning mode, doesn't fail build)

#### Core Build Targets

Standard build operations:

- `ant clean` - Remove all build artifacts
- `ant compile` - Compile source code
- `ant build` - Complete build (compile + jar)
- `ant test` - Run unit tests
- `ant jar` - Create executable JAR file
- `ant run` - Run the application
- `ant javadoc` - Generate API documentation

#### Dependency Management

- `ant resolve` - Download dependencies using Ivy
- `ant report` - Generate dependency report
- `ant clean-ivy` - Remove downloaded dependencies

#### Utility Targets

- `ant summary` - **Display all available targets** with descriptions
- `ant validate-ci-parity` - **Ensure local/CI consistency**

#### Target Usage Examples

**Daily Development Workflow:**
```bash
# Fast iteration during coding
ant quick-test

# Check your work before committing
ant pre-commit

# Full local validation (matches CI)
ant ci-pipeline
```

**Quality Analysis:**
```bash
# Check everything without failing
ant check

# Focus on specific quality aspects
ant coverage
ant checkstyle-warn
ant spotbugs-warn
```

**CI/CD Integration:**
```bash
# Fast checks (format + style)
ant format-check checkstyle

# Complete CI pipeline
ant ci-pipeline
```

#### Progressive Validation Strategy

The build targets follow a **progressive validation** approach:

1. **Development** (`quick-test`, `dev-check`) - Fast feedback, warnings only
2. **Pre-commit** (`pre-commit`, `check`) - Comprehensive but non-blocking
3. **CI/CD** (`ci-check`, `ci-pipeline`) - Strict enforcement, fails on violations

This ensures developers get fast feedback locally while maintaining strict quality gates in CI/CD.

### Testing

The project uses JUnit 5 for testing, with AssertJ for assertions and Mockito for mocking.

#### Test Structure
- Unit tests are located in `test/java/`
- Test resources are in `test/resources/`
- Tests follow the standard naming convention: `*Test.java`

#### Running Tests
- From command line: `ant test`
- From IDE: Run the test classes directly

#### Code Coverage
Code coverage reports are generated using JaCoCo:
1. Run `ant coverage`
2. Open `reports/coverage/html/index.html` in a browser

#### Code Style
This project follows the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).
Checkstyle is used to enforce these standards.

##### Running Checkstyle
- `ant checkstyle-warn` - Run Checkstyle analysis without failing the build
- `ant checkstyle` - Run Checkstyle analysis and fail on violations
- `ant check` - Run all checks including Checkstyle

The Checkstyle report is generated in `reports/checkstyle/checkstyle-report.html`.

#### Static Analysis
This project uses SpotBugs for static analysis to detect potential bugs and code quality issues.

##### Running SpotBugs
- `ant spotbugs-warn` - Run SpotBugs analysis without failing the build
- `ant spotbugs` - Run SpotBugs analysis and fail on violations
- `ant check` - Run all checks including SpotBugs (in warning mode)

SpotBugs reports are generated in both HTML and XML formats:
- HTML report: `reports/spotbugs/spotbugs-report.html`
- XML report: `reports/spotbugs/spotbugs-result.xml`

The SpotBugs configuration can be customized by editing:
- `config/spotbugs/spotbugs.xml` - Main configuration settings
- `config/spotbugs/spotbugs-exclude.xml` - Exclude filters for false positives

##### Automatic Code Formatting
The project uses Google Java Format to automatically format code according to the style guide:

- `ant format` - Format all Java source files
- `ant format-check` - Check if files are properly formatted without modifying them

**Before submitting code reviews or pull requests:**
1. Run `ant format` to ensure your code follows the project's style guidelines
2. Run `ant check` to verify there are no remaining issues
3. Commit your changes only after formatting and verification

This ensures consistent code style across the project and makes code reviews more efficient by focusing on substance rather than style.

### Creating Releases

To create a release build:

1. Update version number in `src/FST.java`
2. Update the README.md with release notes
3. Run `ant clean jar`
4. The release JAR will be in the `dist/` directory

### Continuous Integration & Deployment

FST uses **GitHub Actions** for CI/CD with an **Ant as Single Source of Truth** architecture. This ensures identical behavior between local development and CI environments.

#### CI/CD Architecture

**Key Principles:**
- **Single Source of Truth**: All build logic in `build.xml`
- **Local/CI Parity**: "If it works locally, it works in CI"
- **Zero Duplication**: GitHub Actions calls Ant targets directly
- **Progressive Validation**: Fast checks → comprehensive testing → artifact generation

#### GitHub Actions Workflow

The CI/CD pipeline consists of three main phases:

1. **Fast Checks** (5 minutes)
   - Code formatting validation
   - Style checking with Checkstyle
   - Fails fast to provide immediate feedback

2. **Build & Test Matrix** (15 minutes per Java version)
   - Tests on Java 8, 11, 17, and 21
   - Complete CI pipeline: `ant ci-pipeline`
   - Coverage analysis and quality reports

3. **Artifact Generation** (10 minutes per Java version)
   - Multi-Java JAR builds for distribution
   - Only runs after all tests pass

#### Local Development Integration

**The beauty of this approach**: The same commands work locally and in CI:

```bash
# What developers run locally
ant format-check checkstyle    # Fast checks
ant ci-pipeline                # Full CI pipeline

# What GitHub Actions runs
- name: Fast Checks
  run: ant format-check checkstyle

- name: CI Pipeline
  run: ant ci-pipeline
```

#### Multi-Java Compatibility

The project supports multiple Java versions:

| Java Version | Purpose | Artifact |
|--------------|---------|----------|
| Java 8 | Legacy Support | `fst-java8-latest.jar` |
| Java 11 | LTS Support | `fst-java11-latest.jar` |
| Java 17 | **Primary Target** | `fst-java17-latest.jar` |
| Java 21 | Future Compatibility | `fst-java21-latest.jar` |

#### Quality Gates

**Build-Failing Checks:**
- ❌ Code formatting (Google Java Format)
- ❌ Style violations (Checkstyle)

**Warning-Only Checks:**
- ⚠️ Static analysis issues (SpotBugs)
- ⚠️ Coverage analysis (JaCoCo)

#### Artifact Downloads

After successful builds, download artifacts from:
1. **GitHub Actions Tab** → Latest successful run → Artifacts
2. Choose the appropriate Java version for your runtime

#### Setting Up CI/CD

The CI/CD pipeline is already configured. To enable it:

1. **Enable GitHub Actions** in repository settings
2. **Configure branch protection** to require CI success
3. **That's it!** The pipeline uses existing Ant targets

#### Local CI Validation

Validate that your local build matches CI:

```bash
# Ensure local/CI parity
ant validate-ci-parity

# Run the exact same pipeline as CI
ant ci-pipeline
```

#### Benefits of This Architecture

- ✅ **Zero Maintenance**: Changes only needed in `build.xml`
- ✅ **Developer Confidence**: Local success = CI success
- ✅ **Easy Debugging**: Same commands work everywhere
- ✅ **Fast Feedback**: Progressive validation strategy
- ✅ **Multi-Java Support**: Comprehensive compatibility testing

For detailed CI/CD implementation information, see `CI-CD-IMPLEMENTATION-PLAN.md`.

### Troubleshooting Development Issues

#### Common Issues
- **Ivy resolution fails**: Check network connection and proxy settings
- **Tests fail**: Check the test reports in `build/test/results/`
- **Build fails**: Check the console output for specific error messages

#### Getting Help
If you encounter issues or have questions about the development process, please open an issue on the project repository.
