# Junie Guidelines

## Overview
Junie is a JetBrains tool for automating workflows in GitHub repositories. This document provides guidelines for using Junie in this project.

## Setup

### Prerequisites
- GitHub repository with appropriate permissions
- GitHub Actions enabled

### Configuration
1. Ensure the `.github/workflows/junie.yml` file is properly configured
2. Make sure the `junie-runner.jar` is present in the project root

## Usage

### Running Junie
Junie is triggered through GitHub Actions workflow_dispatch events. The workflow can be manually triggered or called from other workflows.

### Parameters
When triggering Junie, you need to provide:
- `run_id`: A unique identifier for the workflow run
- `workflow_params`: JSON-formatted parameters for the workflow

### Example
```yaml
name: Trigger Junie
on:
  push:
    branches: [ main ]
jobs:
  trigger-junie:
    runs-on: ubuntu-latest
    steps:
      - name: Trigger Junie workflow
        uses: peter-evans/repository-dispatch@v2
        with:
          event-type: junie-workflow
          client-payload: '{"run_id": "manual-run-1", "workflow_params": "{\"param1\": \"value1\"}"}'
```

## Best Practices
1. Always provide meaningful `run_id` values to easily identify workflow runs
2. Structure your `workflow_params` JSON properly
3. Test your workflows in a development environment before running them in production
4. Review Junie logs after each run to ensure proper execution

## Troubleshooting
If you encounter issues with Junie:
1. Check that your workflow file is correctly formatted
2. Verify that all required permissions are set
3. Ensure the `junie-runner.jar` is up to date
4. Review GitHub Actions logs for detailed error messages

## References
- [JetBrains Junie Documentation](https://github.com/jetbrains-junie)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)