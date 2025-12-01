# GAMESTORE - Test Execution Instructions

## For Developers Cloning the Repository

If you have just cloned this project and want to run the web application tests, follow these steps:

### Step 1: Navigate to the web directory
```bash
cd Final-Project/web
```

### Step 2: Install dependencies
```bash
npm install
```
This will install all dependencies including Jest and testing libraries.

### Step 3: Run the tests
```bash
npm test
```

### Step 4: Expected result
You should see something like this:
```
 PASS  src/components/__test__/Button.test.jsx
 PASS  src/components/__test__/GameCard.test.jsx
 PASS  src/components/__test__/Header.test.jsx

Test Suites: 3 passed, 3 total
Tests:       15 passed, 15 total
Snapshots:   0 total
Time:        ~2s
```

## Testing Information

### Where are the tests located?
The tests are in: `src/components/__test__/`

### What is tested?
- **Button.test.jsx**: Button component tests (5 tests)
- **GameCard.test.jsx**: GameCard component tests (6 tests)
- **Header.test.jsx**: Header component tests (4 tests)

### Run tests in watch mode (development mode)
```bash
npm test -- --watch
```
The tests will re-run automatically when you change files.

### Run tests only once (without watch)
```bash
npm test -- --watchAll=false
```

## Requirements

- Node.js v16 or higher
- npm or yarn
- React 19.x with @testing-library/react 16+

## Troubleshooting

If you have issues:

1. **Make sure you are in the correct directory**
   ```bash
   cd Final-Project/web
   pwd  # or "cd" on Windows to see the current path
   ```

2. **Reinstall node_modules**
   ```bash
   rm -rf node_modules package-lock.json
   npm install
   ```

3. **Clear Jest cache**
   ```bash
   npm test -- --clearCache
   ```

4. **Verify Node version**
   ```bash
   node --version
   # Should be v16 or higher
   ```

## Additional Documentation

See `README-SETUP.md` in the `web/` directory for more details about project setup and available commands.
