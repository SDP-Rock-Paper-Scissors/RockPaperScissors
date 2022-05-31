module.exports = {
  "env": {
    "browser": true,
    "commonjs": true,
    "es2021": true,
  },
  "extends": [
    "eslint:recommended",
    "plugin:@typescript-eslint/recommended",
  ],
  "parser": "@typescript-eslint/parser",
  "parserOptions": {
    "ecmaVersion": 13,
  },
  "plugins": [
    "@typescript-eslint",
    "import",
  ],
  "rules": {
    "@typescript-eslint/no-non-null-assertion": "off"
  },
  "ignorePatterns": [
    "/lib/**/*",
  ],
};
