#!/bin/bash

# DSL Performance Test Runner
# Runs comprehensive performance tests and generates reports

set -e

echo "=========================================="
echo "DSL Performance Test Suite"
echo "=========================================="
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Default values
HEAP_SIZE="4g"
TEST_SUITE="all"
VERBOSE=false

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        -h|--heap)
            HEAP_SIZE="$2"
            shift 2
            ;;
        -t|--test)
            TEST_SUITE="$2"
            shift 2
            ;;
        -v|--verbose)
            VERBOSE=true
            shift
            ;;
        --help)
            echo "Usage: $0 [OPTIONS]"
            echo ""
            echo "Options:"
            echo "  -h, --heap SIZE      Set JVM heap size (default: 4g)"
            echo "  -t, --test SUITE     Test suite to run: all, basic, comprehensive (default: all)"
            echo "  -v, --verbose        Enable verbose output"
            echo "  --help               Show this help message"
            echo ""
            echo "Examples:"
            echo "  $0                           # Run all tests with default settings"
            echo "  $0 -t basic                  # Run only basic performance tests"
            echo "  $0 -h 8g -t comprehensive    # Run comprehensive tests with 8GB heap"
            exit 0
            ;;
        *)
            echo -e "${RED}Unknown option: $1${NC}"
            echo "Use --help for usage information"
            exit 1
            ;;
    esac
done

# Determine which tests to run
case $TEST_SUITE in
    all)
        TESTS="PerformanceTestSuite,ComprehensiveFunctionBenchmark"
        echo "Running: All performance tests"
        ;;
    basic)
        TESTS="PerformanceTestSuite"
        echo "Running: Basic performance tests"
        ;;
    comprehensive)
        TESTS="ComprehensiveFunctionBenchmark"
        echo "Running: Comprehensive function benchmark"
        ;;
    *)
        echo -e "${RED}Invalid test suite: $TEST_SUITE${NC}"
        echo "Valid options: all, basic, comprehensive"
        exit 1
        ;;
esac

echo "Heap size: $HEAP_SIZE"
echo ""

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo -e "${RED}Error: Maven is not installed or not in PATH${NC}"
    exit 1
fi

# Clean previous test results
echo -e "${YELLOW}Cleaning previous test results...${NC}"
rm -f performance_report_*.txt performance_data_*.csv
echo ""

# Build the project first
echo -e "${YELLOW}Building project...${NC}"
if [ "$VERBOSE" = true ]; then
    mvn clean compile test-compile
else
    mvn clean compile test-compile -q
fi
echo -e "${GREEN}Build successful${NC}"
echo ""

# Run performance tests
echo -e "${YELLOW}Running performance tests...${NC}"
echo "This may take several minutes depending on your system."
echo ""

START_TIME=$(date +%s)

if [ "$VERBOSE" = true ]; then
    mvn test -Dtest="$TESTS" -DargLine="-Xmx$HEAP_SIZE -Xms${HEAP_SIZE%g}g"
else
    mvn test -Dtest="$TESTS" -DargLine="-Xmx$HEAP_SIZE -Xms${HEAP_SIZE%g}g" -q
fi

TEST_EXIT_CODE=$?
END_TIME=$(date +%s)
DURATION=$((END_TIME - START_TIME))

echo ""
echo "=========================================="

if [ $TEST_EXIT_CODE -eq 0 ]; then
    echo -e "${GREEN}Performance tests completed successfully!${NC}"
else
    echo -e "${RED}Performance tests failed with exit code: $TEST_EXIT_CODE${NC}"
    exit $TEST_EXIT_CODE
fi

echo "Duration: ${DURATION} seconds"
echo "=========================================="
echo ""

# List generated reports
echo -e "${YELLOW}Generated Reports:${NC}"
echo ""

if ls performance_report_*.txt 1> /dev/null 2>&1; then
    for report in performance_report_*.txt; do
        SIZE=$(du -h "$report" | cut -f1)
        echo -e "  ${GREEN}✓${NC} $report ($SIZE)"
    done
fi

if ls performance_data_*.csv 1> /dev/null 2>&1; then
    for csv in performance_data_*.csv; do
        SIZE=$(du -h "$csv" | cut -f1)
        echo -e "  ${GREEN}✓${NC} $csv ($SIZE)"
    done
fi

echo ""
echo "=========================================="
echo "Quick Summary"
echo "=========================================="

# Extract key findings from the latest report
LATEST_REPORT=$(ls -t performance_report_*.txt 2>/dev/null | head -1)

if [ -n "$LATEST_REPORT" ]; then
    echo ""
    echo "Top 5 Slowest Functions:"
    grep -A 6 "TOP 10 SLOWEST FUNCTIONS" "$LATEST_REPORT" | tail -5 || echo "  (See full report for details)"
    
    echo ""
    echo "Top 5 Memory-Intensive Functions:"
    grep -A 6 "TOP 10 MEMORY-INTENSIVE FUNCTIONS" "$LATEST_REPORT" | tail -5 || echo "  (See full report for details)"
    
    echo ""
    echo -e "${YELLOW}For detailed analysis, see: $LATEST_REPORT${NC}"
fi

echo ""
echo "=========================================="
echo "Next Steps"
echo "=========================================="
echo ""
echo "1. Review the performance report for detailed metrics"
echo "2. Import the CSV file into Excel/Sheets for visualization"
echo "3. Identify high-complexity functions for optimization"
echo "4. Check scalability analysis for functions with poor scaling"
echo ""
echo "For more information, see: src/test/java/com/filter/dsl/performance/README.md"
echo ""
