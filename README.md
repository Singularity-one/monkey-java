# Monkey 語言 Java 實現 - 快速開始

## 📦 專案設置

### 1. 創建專案目錄結構

```bash
mkdir -p monkey-java/src/{main,test}/java/com/monkey/{token,lexer}
cd monkey-java
```

### 2. 放置文件

將以下文件放在對應的目錄中：

```
monkey-java/
├── pom.xml
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── monkey/
│   │               ├── Main.java
│   │               ├── token/
│   │               │   ├── Token.java
│   │               │   └── TokenType.java
│   │               └── lexer/
│   │                   └── Lexer.java
│   └── test/
│       └── java/
│           └── com/
│               └── monkey/
│                   └── lexer/
│                       └── LexerTest.java
```

## 🚀 編譯和運行

### 編譯專案

```bash
mvn clean compile
```

### 運行測試

```bash
mvn test
```

你應該看到類似這樣的輸出：

```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.monkey.lexer.LexerTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

### 運行 Main 程式

```bash
mvn exec:java -Dexec.mainClass="com.monkey.Main"
```

或者先打包再運行：

```bash
mvn package
java -jar target/monkey-interpreter-1.0-SNAPSHOT.jar
```

## 📝 測試你的 Lexer

創建一個測試文件 `test.monkey`:

```monkey
let x = 5;
let y = 10;

let add = fn(a, b) {
    a + b;
};

if (x < y) {
    return true;
} else {
    return false;
}
```

然後修改 `Main.java` 來讀取這個文件，或者直接在 Main 中測試不同的輸入。

## 🧪 理解 Lexer 的工作原理

Lexer 將原始碼字串轉換為 Token 序列：

**輸入:**
```monkey
let x = 5;
```

**輸出 Tokens:**
```
Token{type=LET, literal='let'}
Token{type=IDENT, literal='x'}
Token{type=ASSIGN, literal='='}
Token{type=INT, literal='5'}
Token{type=SEMICOLON, literal=';'}
Token{type=EOF, literal=''}
```

## 🎯 第一章學習重點

### 已完成的功能：

✅ Token 類型定義（TokenType）  
✅ Token 類實現  
✅ Lexer 基礎結構  
✅ 單字元 token 識別（+, -, *, / 等）  
✅ 雙字元 token 識別（==, !=）  
✅ 識別符號識別  
✅ 關鍵字識別  
✅ 數字識別  
✅ 空白字元處理  
✅ 完整的測試套件

### 核心概念：

1. **readChar()**: 讀取下一個字元
2. **peekChar()**: 偷看下一個字元（不移動位置）
3. **skipWhitespace()**: 跳過空白字元
4. **readIdentifier()**: 讀取完整的識別符號
5. **readNumber()**: 讀取完整的數字
6. **lookupIdent()**: 區分關鍵字和識別符號

## 🔍 除錯技巧

### 1. 打印每個 Token

```java
Lexer lexer = new Lexer(input);
Token tok;
while ((tok = lexer.nextToken()).getType() != TokenType.EOF) {
    System.out.println(tok);
}
```

### 2. 檢查特定位置

在 Lexer 中添加調試輸出：

```java
private void readChar() {
    System.out.printf("Position: %d, Char: '%c'%n", position, ch);
    // ... 原有代碼
}
```

### 3. 使用 IDE 斷點

在關鍵方法設置斷點：
- `nextToken()` 的 switch 語句
- `readIdentifier()` 和 `readNumber()` 的開始
- `peekChar()` 調用處

## 📚 下一步

完成第一章後，你可以：

1. **添加更多 token 類型**（字串、註釋等）
2. **進入第二章**：實現 Parser（語法分析器）
3. **改進錯誤處理**：提供更好的錯誤訊息
4. **添加行號和列號追蹤**：方便調試

## 🎉 恭喜！

你已經完成了解釋器的第一步 - Lexer！這是編譯器/解釋器的重要基礎。繼續加油！💪

## 參考資源

- [Writing An Interpreter In Go](https://interpreterbook.com/) - 原書
- [leogtzr/monkeylangj](https://github.com/leogtzr/monkeylangj) - Java 完整實現參考
- [ThePrimeagen/ts-rust-zig-deez](https://github.com/ThePrimeagen/ts-rust-zig-deez) - 多語言實現

# 第二章：Parsing - 快速開始指南

## 📁 完整目錄結構

```
monkey-java/
├── pom.xml
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── monkey/
│   │               ├── Main.java
│   │               ├── token/
│   │               │   ├── Token.java
│   │               │   └── TokenType.java
│   │               ├── lexer/
│   │               │   └── Lexer.java
│   │               ├── ast/
│   │               │   ├── Node.java
│   │               │   ├── Statement.java
│   │               │   ├── Expression.java
│   │               │   ├── Program.java
│   │               │   ├── Identifier.java
│   │               │   ├── LetStatement.java
│   │               │   ├── ReturnStatement.java
│   │               │   ├── ExpressionStatement.java
│   │               │   ├── IntegerLiteral.java
│   │               │   ├── PrefixExpression.java
│   │               │   ├── InfixExpression.java
│   │               │   ├── BooleanLiteral.java
│   │               │   ├── BlockStatement.java
│   │               │   ├── IfExpression.java
│   │               │   ├── FunctionLiteral.java
│   │               │   └── CallExpression.java
│   │               └── parser/
│   │                   └── Parser.java
│   └── test/
│       └── java/
│           └── com/
│               └── monkey/
│                   ├── lexer/
│                   │   └── LexerTest.java
│                   └── parser/
│                       └── ParserTest.java
```

## 🚀 編譯和運行

### 1. 編譯專案

```bash
mvn clean compile
```

### 2. 運行所有測試

```bash
mvn test
```

你應該看到所有測試通過：

```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.monkey.lexer.LexerTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.monkey.parser.ParserTest
[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

### 3. 運行 Demo 程式

```bash
mvn exec:java -Dexec.mainClass="com.monkey.Main"
```

## 🎯 第二章核心概念

### 1. AST（抽象語法樹）

AST 是程式碼的樹狀結構表示：

```
let x = 5 + 10;

變成 AST:

Program
└── LetStatement
    ├── Name: Identifier(x)
    └── Value: InfixExpression
        ├── Left: IntegerLiteral(5)
        ├── Operator: "+"
        └── Right: IntegerLiteral(10)
```

### 2. Pratt Parsing

Pratt Parsing 是一種優雅的解析技術，特別適合處理表達式和運算符優先級：

**核心思想：**
- 每個 token 都有關聯的解析函數
- 使用優先級來決定如何組合表達式
- 前綴解析函數：處理出現在開頭的運算符（如 `-5`, `!true`）
- 中綴解析函數：處理出現在中間的運算符（如 `5 + 5`）

**運算符優先級（從低到高）：**
```
1. LOWEST
2. EQUALS      (==, !=)
3. LESSGREATER (<, >)
4. SUM         (+, -)
5. PRODUCT     (*, /)
6. PREFIX      (-X, !X)
7. CALL        (函數調用)
```

### 3. 解析流程示例

**輸入：** `3 + 4 * 5`

**解析過程：**
1. 解析 `3` → IntegerLiteral(3)
2. 看到 `+` → 中綴運算符，優先級 SUM(4)
3. 解析右側，當前優先級是 4
4. 解析 `4` → IntegerLiteral(4)
5. 看到 `*` → 優先級 PRODUCT(5) > SUM(4)
6. 繼續解析右側 → `4 * 5`
7. 最終：`(3 + (4 * 5))`

## 🧪 測試解析結果

### 簡單表達式

```java
Input:  "5 + 5"
Output: "(5 + 5)"

Input:  "5 + 5 * 2"
Output: "(5 + (5 * 2))"

Input:  "(5 + 5) * 2"
Output: "((5 + 5) * 2)"
```

### Let 語句

```java
Input:  "let x = 5;"
AST:    let x = 5;
```

### If 表達式

```java
Input:  "if (x < y) { x } else { y }"
AST:    if(x < y) xelse y
```

### 函數

```java
Input:  "fn(x, y) { x + y; }"
AST:    fn(x, y) (x + y)

Input:  "add(1, 2 * 3)"
AST:    add(1, (2 * 3))
```

## 🔍 調試技巧

### 1. 查看 AST 結構

```java
Lexer lexer = new Lexer("5 + 5 * 2");
Parser parser = new Parser(lexer);
Program program = parser.parseProgram();

System.out.println(program.string());  // 輸出：(5 + (5 * 2))
```

### 2. 檢查解析錯誤

```java
if (!parser.getErrors().isEmpty()) {
    for (String error : parser.getErrors()) {
        System.err.println("Parser error: " + error);
    }
}
```

### 3. 單步調試

在 Parser 的關鍵方法設置斷點：
- `parseExpression()`：查看表達式解析過程
- `parseStatement()`：查看語句解析
- `nextToken()`：追蹤 token 流

## 📊 測試覆蓋

第二章實現了以下所有測試：

✅ Let 語句解析  
✅ Return 語句解析  
✅ 識別符號表達式  
✅ 整數字面值表達式  
✅ 前綴表達式（`-`, `!`）  
✅ 中綴表達式（`+`, `-`, `*`, `/`, `==`, `!=`, `<`, `>`）  
✅ 運算符優先級  
✅ 布林值（`true`, `false`）  
✅ 分組表達式（括號）  
✅ If/Else 表達式  
✅ 函數字面值  
✅ 函數參數解析  
✅ 函數調用表達式

## 🎓 學習重點

### Parser 的核心責任

1. **讀取 Token 序列** - 從 Lexer 獲取 token
2. **建立 AST** - 根據語法規則建立樹狀結構
3. **錯誤處理** - 收集並報告語法錯誤
4. **優先級處理** - 正確處理運算符優先級

### Pratt Parsing 的優勢

- **簡潔** - 代碼少，易於理解
- **可擴展** - 容易添加新的運算符
- **優雅** - 自然處理優先級
- **高效** - 只需要單次遍歷

## 🔧 常見問題

### Q: 為什麼需要 prefixParseFns 和 infixParseFns？

A: 不同位置的 token 有不同的解析方式：
- `-` 在開頭是前綴運算符：`-5`
- `-` 在中間是中綴運算符：`5 - 3`

### Q: 為什麼表達式也是語句？

A: Monkey 語言中，表達式可以單獨作為語句出現，例如：
```monkey
5 + 5;  // 這是一個表達式語句
```

### Q: 如何添加新的運算符？

1. 在 TokenType 中添加新的 token 類型
2. 在 Lexer 中識別新的運算符
3. 在 Parser 中註冊對應的解析函數
4. 設置適當的優先級

## 🎉 完成第二章！

你現在已經完成了：

✅ 完整的 AST 節點系統  
✅ 功能完整的 Parser  
✅ Pratt Parsing 實現  
✅ 運算符優先級處理  
✅ 完整的測試套件

## 📚 下一步

完成第二章後，你可以：

1. **第三章**：實現 Evaluator（求值器）
2. **實驗**：嘗試添加新的語法特性
3. **優化**：改進錯誤訊息
4. **擴展**：添加字串、陣列等數據類型

繼續加油！你正在建造一個完整的程式語言解釋器！🚀

# 第三章：Evaluation - 快速開始指南

## 🎯 你現在擁有什麼

**一個完整運行的程式語言解釋器！** 🎉

你可以執行：
- 數學運算
- 變數綁定
- 函數定義和調用
- 條件語句
- 閉包
- 遞迴

## 📁 完整目錄結構

```
monkey-java/
├── pom.xml
└── src/
    ├── main/
    │   └── java/
    │       └── com/
    │           └── monkey/
    │               ├── Main.java
    │               ├── token/
    │               │   ├── Token.java
    │               │   └── TokenType.java
    │               ├── lexer/
    │               │   └── Lexer.java
    │               ├── ast/
    │               │   ├── (14 個 AST 節點類)
    │               ├── parser/
    │               │   └── Parser.java
    │               ├── object/
    │               │   ├── MonkeyObject.java
    │               │   ├── ObjectType.java
    │               │   ├── IntegerObject.java
    │               │   ├── BooleanObject.java
    │               │   ├── NullObject.java
    │               │   ├── ReturnValue.java
    │               │   ├── ErrorObject.java
    │               │   ├── FunctionObject.java
    │               │   └── Environment.java
    │               └── evaluator/
    │                   └── Evaluator.java
    └── test/
        └── java/
            └── com/
                └── monkey/
                    ├── lexer/
                    │   └── LexerTest.java
                    ├── parser/
                    │   └── ParserTest.java
                    └── evaluator/
                        └── EvaluatorTest.java
```

## 🚀 編譯和運行

### 1. 編譯

```bash
mvn clean compile
```

### 2. 運行所有測試

```bash
mvn test
```

你應該看到所有測試通過：
```
[INFO] Tests run: 5, Failures: 0  (LexerTest)
[INFO] Tests run: 12, Failures: 0 (ParserTest)
[INFO] Tests run: 11, Failures: 0 (EvaluatorTest)
[INFO] BUILD SUCCESS
```

### 3. 運行 Demo

```bash
mvn exec:java -Dexec.mainClass="com.monkey.Main" -Dexec.args="--demo"
```

### 4. 啟動 REPL

```bash
mvn exec:java -Dexec.mainClass="com.monkey.Main"
```

或打包後運行：
```bash
mvn package
java -jar target/monkey-interpreter-1.0-SNAPSHOT.jar
```

## 🎮 REPL 使用示例

```monkey
Hello! This is the Monkey programming language!
Feel free to type in commands
              __,__
     .--.  .-"     "-.  .--.
    / .. \/  .-. .-.  \/ .. \
   | |  '|  /   Y   \  |'  | |
   | \   \  \ 0 | 0 /  /   / |
    \ '- ,\.-"`` ``"-./, -' /
     `'-' /_   ^ ^   _\ '-'`
         |  \._   _./  |
         \   \ `~` /   /
          '._ '-=-' _.'
             '~---~'

>> 5 + 5
10
>> let a = 10
10
>> let b = 20
20
>> a + b
30
>> let add = fn(x, y) { x + y }
fn(x, y) {
(x + y)
}
>> add(5, 10)
15
>> exit
Goodbye!
```

## 📝 Monkey 程式範例

### 1. 變數和運算

```monkey
let x = 5;
let y = 10;
let sum = x + y;
sum;  // 15
```

### 2. 函數

```monkey
let add = fn(a, b) {
    a + b;
};

add(5, 10);  // 15
```

### 3. 閉包

```monkey
let newAdder = fn(x) {
    fn(y) { x + y };
};

let addTwo = newAdder(2);
addTwo(3);  // 5
```

### 4. 條件語句

```monkey
let max = fn(a, b) {
    if (a > b) {
        a
    } else {
        b
    }
};

max(10, 5);  // 10
```

### 5. 遞迴 - 階乘

```monkey
let factorial = fn(n) {
    if (n == 0) {
        1
    } else {
        n * factorial(n - 1)
    }
};

factorial(5);  // 120
```

### 6. 遞迴 - 費波那契數列

```monkey
let fibonacci = fn(n) {
    if (n == 0) {
        0
    } else {
        if (n == 1) {
            1
        } else {
            fibonacci(n - 1) + fibonacci(n - 2)
        }
    }
};

fibonacci(10);  // 55
```

## 🎯 第三章核心概念

### 1. Tree-Walking Interpreter

直接遍歷 AST 並執行：

```
AST Node → Eval → Object
```

### 2. Object System

所有運行時的值都是 `MonkeyObject`：

- **IntegerObject**: 整數（`5`, `10`）
- **BooleanObject**: 布林（`true`, `false`）
- **NullObject**: 空值
- **FunctionObject**: 函數
- **ReturnValue**: Return 語句的值
- **ErrorObject**: 錯誤

### 3. Environment（環境）

儲存變數綁定，支援作用域鏈：

```
outer env: { x: 5 }
  ↑
inner env: { y: 10 }
```

查找變數時，先在當前環境找，找不到就往外層找。

### 4. 求值順序

```
1. 求值表達式 → 返回 Object
2. 處理語句 → 可能產生副作用（變數綁定）
3. 遇到 Return → 停止執行，返回值
4. 遇到錯誤 → 停止執行，傳播錯誤
```

## 🔍 實現細節

### 錯誤傳播

一旦產生錯誤，立即停止執行並向上傳播：

```java
if (isError(evaluated)) {
    return evaluated;
}
```

### Return 語句處理

用 `ReturnValue` 包裝返回值，讓它能穿透多層區塊：

```java
if (result instanceof ReturnValue) {
    return ((ReturnValue) result).getValue();
}
```

### 閉包實現

函數物件保存定義時的環境：

```java
new FunctionObject(parameters, body, env)
```

調用時創建新環境，並以定義時的環境為外層：

```java
Environment extendedEnv = newEnclosedEnvironment(fn.getEnv());
```

## 🧪 測試覆蓋

第三章實現了以下測試：

✅ 整數表達式求值  
✅ 布林表達式求值  
✅ ! 運算符  
✅ If/Else 表達式  
✅ Return 語句  
✅ 錯誤處理  
✅ Let 語句  
✅ 函數物件  
✅ 函數應用  
✅ 閉包

## 🎓 學習重點

### Evaluator 的核心

1. **遞迴求值** - `eval()` 遞迴調用自己
2. **模式匹配** - 根據節點類型選擇求值方法
3. **環境管理** - 正確處理作用域
4. **錯誤處理** - 及時檢查並傳播錯誤

### 關鍵設計決策

**為什麼用單例 TRUE/FALSE？**
- 節省記憶體
- 可以用 `==` 比較

**為什麼需要 ReturnValue？**
- 區分普通值和 return 語句的值
- 讓 return 能穿透多層區塊

**為什麼錯誤要立即傳播？**
- 避免在錯誤狀態下繼續執行
- 及早發現問題

## 🎉 恭喜！

你已經完成了前三章，擁有了一個功能完整的解釋器！

現在你可以：

✅ 詞法分析（Lexing）  
✅ 語法分析（Parsing）  
✅ 求值（Evaluation）

這已經是一個完整的程式語言了！🚀

## 📚 下一步

雖然書還有更多章節（字串、陣列、內建函數等），但你已經掌握了核心概念。可以嘗試：

1. **添加新的數據類型**（字串、陣列）
2. **添加內建函數**（len, first, last, push）
3. **改進 REPL**（添加歷史記錄、自動完成）
4. **優化性能**（快取、尾調用優化）
5. **添加更多語法**（for 循環、while 循環）

你已經建造了一個真正的程式語言解釋器！👏