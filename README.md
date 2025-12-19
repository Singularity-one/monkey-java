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

# 第四章：Extending the Interpreter - 快速開始指南

## 🎯 新增特性

### 1. 字串（Strings）
```monkey
"Hello World"
"Hello" + " " + "World"  // "Hello World"
len("hello")              // 5
```

### 2. 陣列（Arrays）
```monkey
[1, 2, 3, 4]
[1, 2 * 2, 3 + 3]        // [1, 4, 6]
let arr = [1, 2, 3]
arr[0]                    // 1
arr[1]                    // 2
```

### 3. 雜湊表（Hash Maps）
```monkey
{"name": "John", "age": 30}
let person = {"name": "John", "age": 30}
person["name"]            // "John"
person["age"]             // 30

// 支援多種鍵類型
{
    "string": 1,
    10: 2,
    true: 3,
    false: 4
}
```

### 4. 內建函數（Built-in Functions）

#### len(obj) - 取得長度
```monkey
len("hello")      // 5
len([1, 2, 3])    // 3
len([])           // 0
```

#### first(array) - 取得第一個元素
```monkey
first([1, 2, 3])  // 1
first([])         // null
```

#### last(array) - 取得最後一個元素
```monkey
last([1, 2, 3])   // 3
last([])          // null
```

#### rest(array) - 取得除第一個外的所有元素
```monkey
rest([1, 2, 3])   // [2, 3]
rest([1])         // []
rest([])          // null
```

#### push(array, element) - 添加元素
```monkey
push([1, 2], 3)   // [1, 2, 3]
push([], 1)       // [1]
```

#### puts(...) - 打印輸出
```monkey
puts("Hello", "World")  // 打印兩行
puts(1, 2, 3)           // 打印 1 2 3
```

## 📁 完整檔案列表

新增或修改的文件：

**Token & Lexer:**
- `TokenType.java` - 新增 STRING, LBRACKET, RBRACKET, COLON
- `Lexer.java` - 新增 readString() 方法

**AST 節點:**
- `StringLiteral.java` - 字串字面值
- `ArrayLiteral.java` - 陣列字面值
- `IndexExpression.java` - 索引表達式
- `HashLiteral.java` - 雜湊字面值

**Object 系統:**
- `ObjectType.java` - 新增 STRING, ARRAY, HASH, BUILTIN
- `StringObject.java` - 字串物件
- `ArrayObject.java` - 陣列物件
- `HashObject.java` - 雜湊物件
- `BuiltinFunction.java` - 內建函數物件
- `Hashable.java` - 可雜湊接口
- `HashKey.java` - 雜湊鍵
- `IntegerObject.java` - 實現 Hashable
- `BooleanObject.java` - 實現 Hashable

**Evaluator:**
- `Evaluator.java` - 擴展支援新類型
- `Builtins.java` - 定義所有內建函數

**Parser:**
- `Parser.java` - 新增解析方法

**Tests:**
- `Chapter4Test.java` - 完整測試

## 🚀 編譯和運行

```bash
# 編譯
mvn clean compile

# 運行測試
mvn test

# 運行 Demo
mvn exec:java -Dexec.mainClass="com.monkey.Main" -Dexec.args="--demo"

# 啟動 REPL
mvn exec:java -Dexec.mainClass="com.monkey.Main"
```

## 🎮 REPL 範例

```monkey
>> let name = "Alice"
Alice
>> let greeting = "Hello, " + name + "!"
Hello, Alice!
>> len(greeting)
13

>> let numbers = [1, 2, 3, 4, 5]
[1, 2, 3, 4, 5]
>> first(numbers)
1
>> last(numbers)
5
>> rest(numbers)
[2, 3, 4, 5]

>> let person = {"name": "Bob", "age": 25}
{name: Bob, age: 25}
>> person["name"]
Bob
>> person["age"]
25
```

## 📝 進階範例

### 1. Map 函數（高階函數）

```monkey
let map = fn(arr, f) {
    let iter = fn(arr, accumulated) {
        if (len(arr) == 0) {
            accumulated
        } else {
            iter(rest(arr), push(accumulated, f(first(arr))))
        }
    };
    iter(arr, [])
};

let double = fn(x) { x * 2 };
map([1, 2, 3, 4], double);  // [2, 4, 6, 8]
```

### 2. Reduce 函數

```monkey
let reduce = fn(arr, initial, f) {
    let iter = fn(arr, result) {
        if (len(arr) == 0) {
            result
        } else {
            iter(rest(arr), f(result, first(arr)))
        }
    };
    iter(arr, initial)
};

let sum = fn(arr) {
    reduce(arr, 0, fn(initial, el) { initial + el })
};

sum([1, 2, 3, 4, 5]);  // 15
```

### 3. Filter 函數

```monkey
let filter = fn(arr, predicate) {
    let iter = fn(arr, accumulated) {
        if (len(arr) == 0) {
            accumulated
        } else {
            if (predicate(first(arr))) {
                iter(rest(arr), push(accumulated, first(arr)))
            } else {
                iter(rest(arr), accumulated)
            }
        }
    };
    iter(arr, [])
};

let isEven = fn(x) { x - (x / 2 * 2) == 0 };
filter([1, 2, 3, 4, 5, 6], isEven);  // [2, 4, 6]
```

### 4. 用雜湊表實現簡單資料庫

```monkey
let users = [
    {"id": 1, "name": "Alice", "age": 25},
    {"id": 2, "name": "Bob", "age": 30},
    {"id": 3, "name": "Charlie", "age": 35}
];

let findById = fn(users, id) {
    let iter = fn(users) {
        if (len(users) == 0) {
            null
        } else {
            let user = first(users);
            if (user["id"] == id) {
                user
            } else {
                iter(rest(users))
            }
        }
    };
    iter(users)
};

let user = findById(users, 2);
puts(user["name"]);  // Bob
puts(user["age"]);   // 30
```

## 🎯 核心概念

### 1. Hashable 介面

只有實現 `Hashable` 介面的類型才能作為雜湊鍵：
- IntegerObject
- BooleanObject
- StringObject

```java
public interface Hashable {
    HashKey hashKey();
}
```

### 2. 內建函數系統

內建函數是特殊的物件：

```java
public class BuiltinFunction implements MonkeyObject {
    private final BuiltinFn fn;
    
    @FunctionalInterface
    public interface BuiltinFn {
        MonkeyObject apply(List<MonkeyObject> args);
    }
}
```

### 3. 索引表達式

統一處理陣列和雜湊的索引：

```monkey
array[index]  // 陣列索引
hash[key]     // 雜湊鍵查找
```

## 🔍 實現細節

### 字串連接

使用 `+` 運算符：

```java
if (left.type() == ObjectType.STRING && right.type() == ObjectType.STRING) {
    String leftVal = ((StringObject) left).getValue();
    String rightVal = ((StringObject) right).getValue();
    return new StringObject(leftVal + rightVal);
}
```

### 陣列是不可變的

內建函數返回新陣列而不是修改原陣列：

```monkey
let a = [1, 2, 3]
let b = push(a, 4)  // b = [1, 2, 3, 4], a 仍然是 [1, 2, 3]
```

### 雜湊表使用 HashMap

```java
Map<HashKey, HashPair> pairs = new HashMap<>();
```

## 🧪 測試覆蓋

第四章測試：

✅ 字串字面值  
✅ 字串連接  
✅ 陣列字面值  
✅ 陣列索引  
✅ 雜湊字面值  
✅ 雜湊索引  
✅ 內建函數 len  
✅ 內建函數 first, last, rest, push  
✅ 內建函數 puts

## 🎉 恭喜！

你現在擁有一個功能豐富的程式語言！

✅ 詞法分析  
✅ 語法分析  
✅ 求值  
✅ 字串  
✅ 陣列  
✅ 雜湊表  
✅ 內建函數  
✅ 高階函數  
✅ 閉包

## 🚀 可能的擴展

雖然第四章已經很完整了，你還可以添加：

1. **更多內建函數**
    - `split(str, delimiter)` - 分割字串
    - `join(array, separator)` - 連接陣列
    - `reverse(array)` - 反轉陣列

2. **錯誤處理改進**
    - 更詳細的錯誤訊息
    - 堆疊追蹤

3. **性能優化**
    - 尾調用優化
    - 常量折疊

4. **新語法特性**
    - for 迴圈
    - while 迴圈
    - break/continue

你已經完成了一個完整的、實用的程式語言解釋器！👏🎊

# Writing A Compiler In Java - 第一章指南

## 📖 第一章：Compilers & Virtual Machines

### ⚠️ 重要：第一章是純理論章節

第一章**沒有代碼實現**，只有概念介紹和一個 50 行的 JavaScript 示例來說明 VM 概念。

---

## 🎯 第一章內容概要

### 1. 什麼是編譯器？

**定義：**
> 編譯器是將一種程式語言（源語言）轉換為另一種程式語言（目標語言）的程式。

**核心概念：**
- 編譯器是**翻譯器**
- 實現程式語言的兩種方式：
   1. **解釋** - 逐行執行
   2. **編譯** - 翻譯後執行

**編譯器架構：**
```
Source Code → Lexer → Parser → AST
                                 ↓
                            Optimizer (可選)
                                 ↓
                            Code Generator
                                 ↓
                            Target Code
```

---

### 2. 真實機器如何工作？

**Von Neumann 架構：**

基本組件：
- **CPU** - 中央處理器
   - ALU（算術邏輯單元）
   - 寄存器（Registers）
   - 控制單元
- **記憶體（Memory/RAM）**
- **輸入/輸出設備**

**Fetch-Decode-Execute 循環：**
```
1. Fetch（取指令）   - 從記憶體取得指令
2. Decode（解碼）    - 識別要執行什麼操作
3. Execute（執行）   - 執行指令
4. 回到步驟 1
```

---

### 3. 記憶體和堆疊

**記憶體地址：**
- CPU 使用**數字**作為地址訪問記憶體
- 類似陣列索引的概念
- 每個位置稱為一個「字（Word）」

**堆疊（The Stack）：**
```
特性：
- LIFO（後進先出）
- 用於實現調用堆疊（Call Stack）
- 儲存：
  * 返回地址（Return Address）
  * 函數參數
  * 局部變數
```

**寄存器（Registers）：**
- CPU 內部的高速存儲
- 數量少但訪問極快
- 常用寄存器：
   * **堆疊指針（Stack Pointer）** - 指向堆疊頂部
   * 通用寄存器 - 存儲計算結果

---

### 4. 什麼是虛擬機？

**定義：**
> 虛擬機是用軟體建造的電腦，模擬真實電腦的行為。

**虛擬機的組成：**
```java
// 概念示例（來自書中 JavaScript）
{
    programCounter: 0,      // 程式計數器
    stack: [],              // 堆疊
    stackPointer: 0,        // 堆疊指針
    instructions: [...]     // 指令序列
}
```

**執行循環：**
```javascript
while (programCounter < program.length) {
    instruction = program[programCounter];
    // Fetch
    
    decode(instruction);
    // Decode
    
    execute(instruction);
    // Execute
    
    programCounter++;
}
```

---

### 5. 堆疊機 vs 寄存器機

**堆疊機（Stack Machine）：**
- ✅ 更簡單易建
- ✅ 指令更簡單
- ❌ 需要更多指令
- 📝 我們將建造堆疊機

**寄存器機（Register Machine）：**
- ✅ 指令更少更密集
- ✅ 可能更快
- ❌ 更複雜
- ❌ 編譯器更難寫

---

### 6. 什麼是字節碼？

**定義：**
> 字節碼是虛擬機執行的指令序列，由操作碼（Opcode）和操作數（Operands）組成。

**字節碼結構：**
```
[Opcode: 1 byte] [Operand 1] [Operand 2] ...
```

**示例（概念性）：**
```
表達式: (3 + 4) - 5

字節碼:
PUSH 3      # 壓入 3
PUSH 4      # 壓入 4
ADD         # 加法
PUSH 5      # 壓入 5
MINUS       # 減法
```

**特性：**
- **操作碼** - 1 字節寬
- **操作數** - 可變寬度
- **助記符** - 如 PUSH、ADD（人類可讀）
- **二進制** - 實際是數字（0, 1, 2...）

---

### 7. 為什麼要建造虛擬機？

**原因 1：可移植性**
```
一次編譯 → 到處運行
不需要為每個架構重寫編譯器
```

**原因 2：領域特定性**
```
自定義指令集
只包含需要的功能
去除不需要的複雜性
更快！
```

**原因 3：優化機會**
```
專門針對源語言優化
自定義字節碼格式
更緊湊的指令
```

---

### 8. 書中的 JavaScript 示例

這是第一章唯一的代碼示例，用於說明概念：

```javascript
let virtualMachine = function(program) {
    let programCounter = 0;
    let stack = [];
    let stackPointer = 0;
    
    while (programCounter < program.length) {
        let instruction = program[programCounter];
        
        switch (instruction) {
            case PUSH:
                stack[stackPointer] = program[programCounter+1];
                stackPointer++;
                programCounter++;
                break;
            case ADD:
                right = stack[stackPointer-1];
                stackPointer--;
                left = stack[stackPointer-1];
                stackPointer--;
                stack[stackPointer] = left + right;
                stackPointer++;
                break;
            case MINUS:
                right = stack[stackPointer-1];
                stackPointer--;
                left = stack[stackPointer-1];
                stackPointer--;
                stack[stackPointer] = left - right;
                stackPointer++;
                break;
        }
        programCounter++;
    }
    
    console.log("stacktop:", stack[stackPointer-1]);
}
```

**執行示例：**
```javascript
let program = [
    PUSH, 3,
    PUSH, 4,
    ADD,
    PUSH, 5,
    MINUS
];

virtualMachine(program);  // 輸出: stacktop: 2
// 計算: (3 + 4) - 5 = 2
```

---

### 9. 我們的計劃

**同時建造編譯器和虛擬機：**

```
為什麼？
- 先建編譯器：不知道 VM 如何執行
- 先建 VM：不知道要執行什麼

解決方案：
- 從小開始
- 逐步建造
- 立即看到結果
- 快速反饋
```

**建造順序：**
```
1. 定義簡單的字節碼指令
2. 建造小型編譯器
3. 建造小型 VM
4. 測試整個系統
5. 逐步添加功能
```

---

## 🎓 關鍵要點

### 編譯器
- ✅ 翻譯器，不一定生成機器碼
- ✅ 可以有多種目標語言
- ✅ 可以有優化階段

### 虛擬機
- ✅ 軟體模擬的電腦
- ✅ 執行字節碼
- ✅ 領域特定

### 真實機器
- ✅ Fetch-Decode-Execute 循環
- ✅ 記憶體使用數字地址
- ✅ 堆疊用於調用管理
- ✅ 寄存器提供快速存取

### 字節碼
- ✅ 虛擬機的機器語言
- ✅ 操作碼 + 操作數
- ✅ 可以定制化
- ✅ 比真實機器碼簡單

---

## 📚 第一章總結

**沒有代碼實現！**

第一章是純理論：
- 理解編譯器概念
- 理解虛擬機原理
- 理解計算機架構基礎
- 為實際建造做準備

**下一章：**

第二章「Hello Bytecode!」才開始實際編寫代碼：
- 定義第一個操作碼
- 建造最小編譯器
- 建造基礎虛擬機
- 編譯和執行 `1 + 2`

---

## 🎯 學習建議

1. **仔細閱讀第一章** - 理解概念很重要
2. **理解堆疊機制** - 這是核心
3. **理解 Fetch-Decode-Execute** - VM 的心跳
4. **準備好開始編碼** - 第二章開始動手！

讓我們進入第二章，開始真正的建造！🚀

# Writing A Compiler In Java - Chapter 2: Hello Bytecode!

## 🎯 本章目標

**成功編譯並執行 `1 + 2` = `3`** ✅

在第二章中,我們實現了:

1. ✅ 定義字節碼指令格式 (Opcode + Instructions)
2. ✅ 構建最小可用的編譯器 (Compiler)
3. ✅ 實現棧式虛擬機 (VM)
4. ✅ 完整的測試套件
5. ✅ 集成到 REPL

## 📁 項目結構

```
src/main/java/com/monkey/
├── code/                          # 新增 - 字節碼定義
│   ├── Opcode.java               # 操作碼枚舉
│   └── Instructions.java         # 指令集和工具函數
├── compiler/                      # 新增 - 編譯器
│   ├── Compiler.java             # 編譯器主類
│   └── Bytecode.java             # 編譯結果
├── vm/                            # 新增 - 虛擬機
│   └── VM.java                   # 棧式虛擬機
└── Main.java                      # 更新 - 整合編譯器

src/test/java/com/monkey/
├── code/
│   └── InstructionsTest.java    # 字節碼測試
├── compiler/
│   └── CompilerTest.java         # 編譯器測試
└── vm/
    └── VMTest.java               # 虛擬機測試
```

## 🚀 快速開始

### 1. 編譯項目

```bash
cd monkey-java
mvn clean compile
```

### 2. 運行測試

```bash
mvn test
```

你應該看到:

```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.monkey.code.InstructionsTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.monkey.compiler.CompilerTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.monkey.vm.VMTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

### 3. 運行 Demo

```bash
mvn exec:java -Dexec.mainClass="com.monkey.Main" -Dexec.args="--demo"
```

輸出示例:

```
=== Monkey Compiler & VM Demo ===

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Input: 1 + 2

Constants:
  0: 1
  1: 2

Instructions:
0000 OpConstant 0
0003 OpConstant 1
0006 OpAdd

Result: 3
```

### 4. 啟動 REPL

```bash
mvn exec:java -Dexec.mainClass="com.monkey.Main"
```

或打包後運行:

```bash
mvn package
java -jar target/monkey-java-1.0-SNAPSHOT.jar
```

## 💻 使用 REPL

```
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

Mode: Compiler

>> 1
1
>> 2
2
>> 1 + 2
3
>> 5 * 10
50
>> (5 + 10) * 2
30
>> exit
Goodbye!
```

## 📚 核心概念詳解

### 1. 字節碼格式

每條指令由操作碼和可選的操作數組成:

```
指令格式:
[OpCode: 1 byte][Operand 1][Operand 2]...

OpConstant:  [0x00][Index: 2 bytes]
OpAdd:       [0x01]
OpSub:       [0x03]
OpMul:       [0x04]
OpDiv:       [0x05]
```

### 2. 編譯流程

```
源代碼 → Lexer → Parser → AST → Compiler → Bytecode → VM → 結果
```

**示例: `1 + 2`**

```
1. 源代碼:
   "1 + 2"

2. AST:
   Program
     ExpressionStatement
       InfixExpression(+)
         Left: IntegerLiteral(1)
         Right: IntegerLiteral(2)

3. 編譯:
   Constants: [Integer(1), Integer(2)]
   Instructions:
     0000 OpConstant 0  ; 載入 1
     0003 OpConstant 1  ; 載入 2
     0006 OpAdd         ; 執行加法

4. 執行:
   stack: []
   → OpConstant 0 → stack: [1]
   → OpConstant 1 → stack: [1, 2]
   → OpAdd        → stack: [3]

5. 結果: 3
```

### 3. 堆疊機執行

VM 使用堆疊來存儲中間值:

```java
// 堆疊指針約定
private int sp = 0;  // 始終指向下一個空閒位置

// 堆疊頂部在 stack[sp-1]
public MonkeyObject stackTop() {
    if (sp == 0) return null;
    return stack[sp - 1];
}

// Push: 先存儲,再遞增
private void push(MonkeyObject obj) {
    stack[sp] = obj;
    sp++;
}

// Pop: 先遞減,再讀取
private MonkeyObject pop() {
    sp--;
    return stack[sp];
}
```

### 4. 大端序編碼

操作數使用大端序(Big-Endian):

```java
// 編碼 65534 → [0xFF, 0xFE]
private static void putUint16(byte[] arr, int offset, int value) {
    arr[offset]     = (byte) ((value >> 8) & 0xFF);  // 高字節
    arr[offset + 1] = (byte) (value & 0xFF);         // 低字節
}

// 解碼 [0xFF, 0xFE] → 65534
public static int readUint16(byte[] ins) {
    return ((ins[0] & 0xFF) << 8) | (ins[1] & 0xFF);
}
```

## 🔍 代碼走讀

### Instructions.make() - 創建字節碼

```java
// 創建 OpConstant 指令
byte[] ins = Instructions.make(Opcode.OP_CONSTANT, 65534);
// 結果: [0x00, 0xFF, 0xFE]
//       ^^^^  ^^^^^^^^^^^
//       操作碼  操作數(索引)

// 創建 OpAdd 指令
byte[] ins = Instructions.make(Opcode.OP_ADD);
// 結果: [0x01]
//       ^^^^
//       操作碼
```

### Compiler.compile() - 編譯邏輯

```java
// 編譯整數字面量
if (node instanceof IntegerLiteral) {
    IntegerLiteral intLit = (IntegerLiteral) node;
    // 1. 創建對象
    IntegerObject integer = new IntegerObject(intLit.getValue());
    // 2. 添加到常量池
    int index = addConstant(integer);
    // 3. 發射 OpConstant 指令
    emit(Opcode.OP_CONSTANT, index);
}

// 編譯中綴表達式
if (node instanceof InfixExpression) {
    InfixExpression infix = (InfixExpression) node;
    // 1. 編譯左操作數
    compile(infix.getLeft());
    // 2. 編譯右操作數
    compile(infix.getRight());
    // 3. 發射運算符指令
    switch (infix.getOperator()) {
        case "+": emit(Opcode.OP_ADD); break;
        case "-": emit(Opcode.OP_SUB); break;
        case "*": emit(Opcode.OP_MUL); break;
        case "/": emit(Opcode.OP_DIV); break;
    }
}
```

### VM.run() - 執行循環

```java
public void run() throws VMException {
    for (int ip = 0; ip < instructions.size(); ip++) {
        // 取指
        Opcode op = Opcode.fromByte(instructions.get(ip));

        switch (op) {
            case OP_CONSTANT:
                // 解碼
                int index = readUint16(instructions, ip + 1);
                ip += 2;
                // 執行
                push(constants.get(index));
                break;

            case OP_ADD:
                // 執行
                MonkeyObject right = pop();
                MonkeyObject left = pop();
                long result = 
                    ((IntegerObject)left).getValue() + 
                    ((IntegerObject)right).getValue();
                push(new IntegerObject(result));
                break;
        }
    }
}
```

## 📊 性能對比

雖然功能還很簡單,但已經可以看到編譯器的優勢:

### 解釋器執行 `1 + 2`:
```
1. 遍歷 AST 找到 InfixExpression
2. 遞歸 Eval(left) → Integer(1)
3. 遞歸 Eval(right) → Integer(2)
4. 執行加法
5. 返回結果

開銷: AST 遍歷 + 多次方法調用
```

### 編譯器執行 `1 + 2`:
```
1. OpConstant 0  → 直接索引載入
2. OpConstant 1  → 直接索引載入
3. OpAdd         → 直接運算

開銷: 幾乎沒有
```

**編譯器的優勢:**
- ✅ 沒有 AST 遍歷
- ✅ 沒有遞歸調用
- ✅ 直接的堆疊操作
- ✅ 更好的局部性

## 🧪 測試詳解

### Instructions 測試

```java
@Test
public void testMake() {
    // 測試 OpConstant 指令生成
    byte[] ins = Instructions.make(Opcode.OP_CONSTANT, 65534);
    // 期望: [0x00, 0xFF, 0xFE]
    assertArrayEquals(
        new byte[]{0, (byte)255, (byte)254}, 
        ins
    );
}

@Test
public void testInstructionsString() {
    // 測試反彙編
    Instructions ins = new Instructions();
    ins.append(Instructions.make(Opcode.OP_ADD));
    ins.append(Instructions.make(Opcode.OP_CONSTANT, 2));
    
    String expected = 
        "0000 OpAdd\n" +
        "0001 OpConstant 2\n";
    
    assertEquals(expected, ins.toString());
}
```

### Compiler 測試

```java
@Test
public void testIntegerArithmetic() {
    CompilerTestCase test = new CompilerTestCase(
        "1 + 2",
        new Object[]{1, 2},  // 常量池
        new byte[][]{
            Instructions.make(Opcode.OP_CONSTANT, 0),
            Instructions.make(Opcode.OP_CONSTANT, 1),
            Instructions.make(Opcode.OP_ADD)
        }
    );
    runCompilerTests(new CompilerTestCase[]{test});
}
```

### VM 測試

```java
@Test
public void testIntegerArithmetic() {
    VMTestCase[] tests = {
        new VMTestCase("1", 1),
        new VMTestCase("2", 2),
        new VMTestCase("1 + 2", 3),
        new VMTestCase("2 * (5 + 10)", 30)
    };
    runVMTests(tests);
}
```

## 💡 關鍵設計決策

### 1. 為什麼選擇堆疊機?

**優點:**
- ✅ 實現簡單
- ✅ 概念清晰
- ✅ 指令集小
- ✅ 易於理解和調試

**缺點:**
- ❌ 比寄存器機稍慢
- ❌ 需要更多 push/pop

**選擇理由:**
對於學習來說,簡單和清晰比性能更重要!

### 2. 為什麼使用常量池?

**沒有常量池:**
```
OpPush 1      // 需要編碼整數到指令中
OpPush 2
OpAdd
```

**使用常量池:**
```
OpConstant 0  // 只需 2 字節索引
OpConstant 1
OpAdd

Constants: [Integer(1), Integer(2)]
```

**優點:**
- ✅ 指令更短
- ✅ 支持複雜對象 (字符串、函數)
- ✅ 常量可以共享
- ✅ 易於優化

### 3. 為什麼用大端序?

```
65534 大端序: [0xFF, 0xFE]  ← 高字節在前
65534 小端序: [0xFE, 0xFF]  ← 低字節在前
```

**選擇大端序:**
- ✅ 更直觀 (符合人類閱讀)
- ✅ 網絡字節序標準
- ✅ 調試時更容易識別
- ✅ 書中使用的格式

## 🎯 學到了什麼?

### 編譯器基礎

1. **字節碼設計** - 操作碼 + 操作數
2. **指令編碼** - 如何將指令序列化為字節
3. **常量池** - 管理編譯時常量
4. **代碼生成** - 從 AST 到字節碼

### 虛擬機基礎

1. **取指-解碼-執行循環** - VM 的核心
2. **堆疊管理** - push/pop 操作
3. **指令執行** - 實現具體的操作
4. **錯誤處理** - 堆疊溢出、除零錯誤

### 軟件工程

1. **模塊化設計** - 清晰的職責分離
2. **測試驅動** - 先寫測試再實現
3. **可調試性** - 反彙編工具
4. **漸進式開發** - 從簡單到複雜

## 🎉 完成第二章!

你現在擁有:

✅ **完整的字節碼系統** - Opcode + Instructions  
✅ **功能編譯器** - AST → Bytecode  
✅ **堆疊虛擬機** - Fetch-Decode-Execute  
✅ **測試套件** - 全面的測試覆蓋  
✅ **可用的 REPL** - 編譯器和解釋器雙模式  
✅ **調試工具** - 反彙編輸出

## 📚 下一步: Chapter 3

第三章將添加:

- **更多運算符** - 比較運算 (<, >, ==, !=)
- **布爾值** - true/false
- **前綴運算符** - !, -
- **OpPop 指令** - 清理堆疊
- **更複雜的表達式**

## 🔧 常見問題

### Q: 為什麼 `1 + 2` 需要 3 條指令?

A: 因為堆疊機架構需要:
1. 將 1 推入堆疊
2. 將 2 推入堆疊
3. 彈出兩個數並相加

### Q: 常量池索引為什麼用 2 字節?

A: 2 字節 = 65536 個常量。足夠大多數程序使用。如果需要更多,可以擴展為 4 字節。

### Q: 可以添加新的運算符嗎?

A: 當然!步驟:
1. 在 `Opcode` 添加新操作碼
2. 在 `Instructions.DEFINITIONS` 添加定義
3. 在 `Compiler` 中生成指令
4. 在 `VM` 中實現執行

### Q: 編譯器比解釋器快多少?

A: 對於簡單運算,大約快 2-3 倍。隨著代碼複雜度增加,差距會更大。

**繼續加油!你正在構建一個真正的編譯器!** 🚀

# Writing A Compiler In Java - Chapter 3: Compiling Expressions

## 🎯 本章目標

在第三章中,我們擴展了編譯器和虛擬機,添加了:

1. ✅ **OpPop 指令** - 清理堆疊
2. ✅ **更多算術運算** - 減法、乘法、除法
3. ✅ **布爾值** - true/false
4. ✅ **比較運算** - ==, !=, >, <
5. ✅ **前綴運算** - !, -
6. ✅ **完整的表達式支持**

## 📁 新增和修改的文件

### 核心文件 (修改)
- `Opcode.java` - 添加新的操作碼
- `Instructions.java` - 更新指令定義
- `Compiler.java` - 添加新的編譯邏輯
- `VM.java` - 添加新的執行邏輯

### Object 系統 (新增)
- `BooleanObject.java` - 布爾值對象

### AST 節點 (新增,如果沒有)
- `BooleanLiteral.java` - 布爾字面量
- `PrefixExpression.java` - 前綴表達式

### 測試文件 (更新)
- `CompilerTest.java` - 新的測試用例
- `VMTest.java` - 新的測試用例

## 🚀 快速開始

### 1. 編譯和測試

```bash
# 編譯
mvn clean compile

# 運行測試
mvn test

# 應該看到所有測試通過
[INFO] Tests run: 2, Failures: 0 (CompilerTest)
[INFO] Tests run: 2, Failures: 0 (VMTest)
```

### 2. 運行 REPL

```bash
mvn exec:java -Dexec.mainClass="com.monkey.Main"
```

嘗試新功能:

```
>> 1 + 2
3
>> 1 - 2
-1
>> 2 * 3
6
>> 10 / 2
5
>> true
true
>> false
false
>> 1 < 2
true
>> 1 > 2
false
>> 1 == 1
true
>> 1 != 2
true
>> !true
false
>> -5
-5
>> !(5 > 3)
false
```

## 📚 核心概念

### 1. OpPop 指令 - 清理堆疊

**問題:** 表達式語句執行後,結果留在堆疊上會導致堆疊溢出

```
1 + 2;  // 結果 3 留在堆疊上
3 + 4;  // 結果 7 也留在堆疊上
5 + 6;  // 堆疊: [3, 7, 11]
...     // 最終堆疊溢出!
```

**解決:** 表達式語句編譯後添加 OpPop

```java
// Compiler.java
if (node instanceof ExpressionStatement) {
    ExpressionStatement exprStmt = (ExpressionStatement) node;
    compile(exprStmt.getExpression());
    emit(Opcode.OP_POP);  // 清理結果
}
```

**效果:**
```
1 + 2;  // 執行後 OpPop,堆疊清空
3 + 4;  // 執行後 OpPop,堆疊清空
```

### 2. 布爾值

布爾值使用單例模式優化記憶體:

```java
// VM.java
public static final BooleanObject TRUE = new BooleanObject(true);
public static final BooleanObject FALSE = new BooleanObject(false);
```

**編譯:**
```
true  → OpTrue
false → OpFalse
```

**執行:**
```java
case OP_TRUE:
    push(TRUE);  // 推入單例對象
    break;
case OP_FALSE:
    push(FALSE);
    break;
```

### 3. 比較運算

#### 支持的運算符

| 運算符 | 操作碼 | 說明 |
|--------|--------|------|
| == | OpEqual | 相等比較 |
| != | OpNotEqual | 不等比較 |
| > | OpGreaterThan | 大於比較 |
| < | (無) | 轉換為 > |

#### < 運算符的巧妙轉換

**問題:** 如果實現 OpLessThan 和 OpGreaterThan,會有重複代碼

**解決:** 只實現 OpGreaterThan,將 < 轉換為 >

```java
// a < b 等價於 b > a
if (infixExpr.getOperator().equals("<")) {
    compile(infixExpr.getRight());  // 先編譯右側
    compile(infixExpr.getLeft());   // 再編譯左側
    emit(Opcode.OP_GREATER_THAN);   // 發射 >
    return;
}
```

**示例:**
```
輸入: 1 < 2
編譯:
  OpConstant 1 (索引為 2)
  OpConstant 0 (索引為 1)
  OpGreaterThan
執行: 2 > 1 = true
```

### 4. 前綴運算

#### ! 運算符 (邏輯非)

```java
case OP_BANG:
    MonkeyObject operand = pop();
    if (operand == TRUE) {
        push(FALSE);
    } else if (operand == FALSE) {
        push(TRUE);
    } else {
        // Monkey: 只有 false 和 null 是 falsy
        // 其他都是 truthy
        push(FALSE);
    }
    break;
```

**示例:**
```
!true  → false
!false → true
!5     → false (5 是 truthy)
!!true → true
```

#### - 運算符 (取負)

```java
case OP_MINUS:
    MonkeyObject operand = pop();
    if (!(operand instanceof IntegerObject)) {
        throw new VMException("unsupported type for negation");
    }
    long value = ((IntegerObject) operand).getValue();
    push(new IntegerObject(-value));
    break;
```

**示例:**
```
-5     → -5
-(-10) → 10
-(1+2) → -3
```

## 🔍 詳細實現

### 編譯流程示例

#### 示例 1: `1 + 2 * 3`

**AST:**
```
InfixExpression(+)
  ├─ IntegerLiteral(1)
  └─ InfixExpression(*)
      ├─ IntegerLiteral(2)
      └─ IntegerLiteral(3)
```

**編譯:**
```
Constants: [1, 2, 3]

Instructions:
0000 OpConstant 0  ; 載入 1
0003 OpConstant 1  ; 載入 2
0006 OpConstant 2  ; 載入 3
0009 OpMul         ; 2 * 3 = 6
0010 OpAdd         ; 1 + 6 = 7
0011 OpPop
```

**執行:**
```
stack: []
→ OpConstant 0 → stack: [1]
→ OpConstant 1 → stack: [1, 2]
→ OpConstant 2 → stack: [1, 2, 3]
→ OpMul        → stack: [1, 6]
→ OpAdd        → stack: [7]
→ OpPop        → stack: []
```

#### 示例 2: `1 < 2`

**編譯:**
```
Constants: [2, 1]  # 注意順序反轉!

Instructions:
0000 OpConstant 0  ; 載入 2
0003 OpConstant 1  ; 載入 1
0006 OpGreaterThan ; 2 > 1
0007 OpPop
```

**執行:**
```
stack: []
→ OpConstant 0 → stack: [2]
→ OpConstant 1 → stack: [2, 1]
→ OpGreaterThan → stack: [true]  # 2 > 1 = true
→ OpPop        → stack: []
```

#### 示例 3: `!true`

**編譯:**
```
Constants: []

Instructions:
0000 OpTrue
0001 OpBang
0002 OpPop
```

**執行:**
```
stack: []
→ OpTrue → stack: [true]
→ OpBang → stack: [false]
→ OpPop  → stack: []
```

### lastPoppedStackElem 的作用

**問題:** OpPop 會清空堆疊,測試如何獲取結果?

**解決:** VM 添加 `lastPoppedStackElem()` 方法

```java
public MonkeyObject lastPoppedStackElem() {
    return stack[sp];  // sp 指向下一個位置
                       // OpPop 後,sp 遞減
                       // 被彈出的元素仍在 stack[sp]
}
```

**測試中使用:**
```java
VM vm = new VM(bytecode);
vm.run();
MonkeyObject result = vm.lastPoppedStackElem();  // 獲取結果
testExpectedObject(expected, result);
```

## 🧪 測試詳解

### 編譯器測試

```java
@Test
public void testBooleanExpressions() {
    CompilerTestCase[] tests = new CompilerTestCase[]{
        // 布爾字面量
        new CompilerTestCase(
            "true",
            new Object[]{},  // 無常量
            new byte[][]{
                Instructions.make(Opcode.OP_TRUE),
                Instructions.make(Opcode.OP_POP)
            }
        ),
        
        // < 運算符轉換
        new CompilerTestCase(
            "1 < 2",
            new Object[]{2, 1},  // 操作數反轉!
            new byte[][]{
                Instructions.make(Opcode.OP_CONSTANT, 0),
                Instructions.make(Opcode.OP_CONSTANT, 1),
                Instructions.make(Opcode.OP_GREATER_THAN),
                Instructions.make(Opcode.OP_POP)
            }
        )
    };
    
    runCompilerTests(tests);
}
```

### 虛擬機測試

```java
@Test
public void testBooleanExpressions() {
    VMTestCase[] tests = new VMTestCase[]{
        // 基本測試
        new VMTestCase("true", true),
        new VMTestCase("false", false),
        
        // 比較運算
        new VMTestCase("1 < 2", true),
        new VMTestCase("1 > 2", false),
        new VMTestCase("1 == 1", true),
        
        // 邏輯非
        new VMTestCase("!true", false),
        new VMTestCase("!!true", true),
        
        // 複雜表達式
        new VMTestCase("(1 < 2) == true", true),
        new VMTestCase("!(5 > 3)", false)
    };
    
    runVMTests(tests);
}
```

## 💡 設計決策

### 1. 為什麼只實現 OpGreaterThan?

**問題:** 需要實現 <, >, <=, >= 四個運算符嗎?

**答案:** 不需要!

```
a < b  ≡ b > a
a <= b ≡ !(a > b)
a >= b ≡ !(b > a)
```

只需實現 OpGreaterThan,其他可以轉換。

**優點:**
- ✅ 減少操作碼數量
- ✅ 減少 VM 代碼
- ✅ 編譯器負責轉換

### 2. 為什麼布爾值用單例?

**原因:**
- 整個程序只需要兩個布爾對象
- 可以用 `==` 直接比較
- 節省記憶體

```java
// 不需要創建新對象
push(TRUE);   // 總是同一個對象
push(FALSE);  // 總是同一個對象

// 可以直接比較
if (obj == TRUE) { ... }
```

### 3. 為什麼需要 OpPop?

**沒有 OpPop 的問題:**
```
1 + 2;
3 + 4;
5 + 6;

堆疊: [3, 7, 11]  // 結果累積
```

**有 OpPop:**
```
1 + 2;  OpPop → 堆疊: []
3 + 4;  OpPop → 堆疊: []
5 + 6;  OpPop → 堆疊: []
```

**結論:** OpPop 確保表達式語句不會污染堆疊

## 📊 新增操作碼總覽

| 操作碼 | 操作數 | 功能 | 堆疊變化 |
|--------|--------|------|----------|
| OpPop | 無 | 彈出頂部 | [a] → [] |
| OpSub | 無 | 減法 | [a,b] → [a-b] |
| OpMul | 無 | 乘法 | [a,b] → [a*b] |
| OpDiv | 無 | 除法 | [a,b] → [a/b] |
| OpTrue | 無 | 推入true | [] → [true] |
| OpFalse | 無 | 推入false | [] → [false] |
| OpEqual | 無 | 相等比較 | [a,b] → [a==b] |
| OpNotEqual | 無 | 不等比較 | [a,b] → [a!=b] |
| OpGreaterThan | 無 | 大於比較 | [a,b] → [a>b] |
| OpBang | 無 | 邏輯非 | [a] → [!a] |
| OpMinus | 無 | 取負 | [a] → [-a] |

## 🎉 完成第三章!

你現在擁有:

✅ **完整的算術運算** - +, -, *, /  
✅ **布爾值系統** - true, false  
✅ **比較運算** - ==, !=, >, <  
✅ **前綴運算** - !, -  
✅ **堆疊管理** - OpPop  
✅ **優化技巧** - 單例、運算符轉換

## 📚 下一步: Chapter 4

第四章將添加:

- **條件語句** - if/else
- **跳轉指令** - OpJump, OpJumpNotTruthy
- **Null 值** - OpNull
- **更複雜的控制流**

## 🔧 常見問題

### Q: 為什麼 < 要轉換為 >?

A: 為了減少 VM 中的代碼重複。只需實現一個比較方向,另一個在編譯時轉換。

### Q: !!5 為什麼是 true?

A: 在 Monkey 中,只有 false 和 null 是 falsy,其他都是 truthy。所以:
- !5 → false (5 是 truthy)
- !!5 → !false → true

### Q: 可以添加 <= 和 >= 嗎?

A: 可以!轉換規則:
```
a <= b → !(a > b)
a >= b → !(b > a)
```

編譯器負責生成額外的 OpBang 指令。

### Q: 為什麼測試用 lastPoppedStackElem?

A: 因為 OpPop 清空了堆疊。lastPoppedStackElem 讓我們能獲取被彈出的值進行測試。

# Writing A Compiler In Java - Chapter 4: Conditionals

## 🎯 本章目標

在第四章中,我們添加了條件語句支持:

1. ✅ **跳轉指令** - OpJump, OpJumpNotTruthy
2. ✅ **Null 值** - OpNull
3. ✅ **if/else 表達式** - 完整的條件語句
4. ✅ **回填技術** - 處理未知的跳轉地址
5. ✅ **OpPop 優化** - 移除不必要的 OpPop

## 📁 新增和修改的文件

### 核心文件 (修改)
- `Opcode.java` - 添加 OpJump, OpJumpNotTruthy, OpNull
- `Instructions.java` - 添加 replaceInstruction, changeOperand, removeLast
- `Compiler.java` - 添加 if 表達式編譯,追蹤最後指令
- `VM.java` - 添加跳轉指令執行,isTruthy 方法

### 新增類
- `EmittedInstruction.java` - 記錄發射的指令信息
- `NullObject.java` - Null 值對象
- `IfExpression.java` - if 表達式 AST 節點
- `BlockStatement.java` - 塊語句 AST 節點

### 測試文件 (更新)
- `CompilerTest.java` - 添加條件語句測試
- `VMTest.java` - 添加條件語句測試

## 🚀 快速開始

### 1. 編譯和測試

```bash
# 編譯
mvn clean compile

# 運行測試
mvn test

# 應該看到所有測試通過
[INFO] Tests run: 3, Failures: 0 (CompilerTest)
[INFO] Tests run: 3, Failures: 0 (VMTest)
```

### 2. 使用 REPL

```bash
mvn exec:java -Dexec.mainClass="com.monkey.Main"
```

測試條件語句:

```
>> if (true) { 10 }
10
>> if (false) { 10 }
null
>> if (1 < 2) { 10 } else { 20 }
10
>> if (1 > 2) { 10 } else { 20 }
20
>> if (5 > 3) { 1 + 2 } else { 3 + 4 }
3
```

## 📚 核心概念

### 1. 跳轉指令

#### OpJump - 無條件跳轉

```
OpJump <address>
```

無條件跳轉到指定地址。

**示例:**
```
0000 OpConstant 0
0003 OpJump 0010     ; 跳到 0010
0006 OpConstant 1    ; 被跳過
0009 OpPop
0010 OpConstant 2    ; 從這裡繼續
```

#### OpJumpNotTruthy - 條件跳轉

```
OpJumpNotTruthy <address>
```

彈出堆疊頂部元素,如果為 falsy 則跳轉。

**示例:**
```
0000 OpTrue
0001 OpJumpNotTruthy 0007  ; true 是 truthy,不跳轉
0004 OpConstant 0          ; 執行這裡
0007 ...
```

### 2. if 表達式編譯

#### 無 else 分支

```monkey
if (condition) { consequence }
```

編譯為:

```
<condition>
OpJumpNotTruthy <afterConsequence>
<consequence>
OpNull                      ; 隱式返回 null
<afterConsequence>:
```

**示例:**
```monkey
if (1 < 2) { 10 }
```

編譯為:
```
0000 OpConstant 0      ; 1
0003 OpConstant 1      ; 2
0006 OpGreaterThan     ; 2 > 1 (注意: < 轉換為 >)
0007 OpJumpNotTruthy 13
0010 OpConstant 2      ; 10
0013 OpNull            ; 如果條件為假
```

#### 有 else 分支

```monkey
if (condition) { consequence } else { alternative }
```

編譯為:

```
<condition>
OpJumpNotTruthy <alternative>
<consequence>
OpJump <afterAlternative>
<alternative>:
<alternative>
<afterAlternative>:
```

**示例:**
```monkey
if (true) { 10 } else { 20 }
```

編譯為:
```
0000 OpTrue
0001 OpJumpNotTruthy 10
0004 OpConstant 0      ; 10
0007 OpJump 13
0010 OpConstant 1      ; 20
0013 ...
```

### 3. 回填技術

**問題:** 編譯跳轉指令時,不知道跳轉目標的地址

**解決:** 先發射帶佔位符的指令,後續回填真實地址

```java
// 1. 發射條件跳轉 (地址未知,用 9999 佔位)
int jumpPos = emit(Opcode.OP_JUMP_NOT_TRUTHY, 9999);

// 2. 編譯 consequence
compile(ifExpr.getConsequence());

// 3. 現在知道跳轉目標了,回填地址
int afterConsequence = instructions.size();
changeOperand(jumpPos, afterConsequence);
```

**changeOperand 實現:**
```java
public void changeOperand(int opPos, int operand) {
    Opcode op = Opcode.fromByte(bytes.get(opPos));
    byte[] newInstruction = make(op, operand);
    replaceInstruction(opPos, newInstruction);
}
```

### 4. OpPop 優化

**問題:** if 表達式的值需要留在堆疊上,但 consequence 和 alternative 編譯時會添加 OpPop

```monkey
if (true) { 10 }  // 10 應該留在堆疊上
```

未優化的編譯結果:
```
OpTrue
OpJumpNotTruthy ...
OpConstant 0   ; 10
OpPop          ; ← 不應該有這個!
...
```

**解決:** 編譯 if 表達式後,移除 consequence 和 alternative 末尾的 OpPop

```java
// 編譯 consequence
compile(ifExpr.getConsequence());

// 移除末尾的 OpPop
if (lastInstructionIs(Opcode.OP_POP)) {
    removeLastPop();
}
```

**removeLastPop 實現:**
```java
private void removeLastPop() {
    if (lastInstruction != null && 
        lastInstruction.getOpcode() == Opcode.OP_POP) {
        instructions.removeLast(1);  // OpPop 是 1 字節
        lastInstruction = previousInstruction;
    }
}
```

### 5. Truthiness (真值判斷)

**Monkey 的真值規則:**

| 值 | Truthy/Falsy |
|----|--------------|
| false | Falsy |
| null | Falsy |
| 0 | **Truthy** ⚠️ |
| "" | **Truthy** ⚠️ |
| 其他 | Truthy |

**注意:** 與 JavaScript 不同,Monkey 中 0 和空字符串是 truthy!

**實現:**
```java
private boolean isTruthy(MonkeyObject obj) {
    if (obj == NULL) return false;
    if (obj == TRUE) return true;
    if (obj == FALSE) return false;
    return true;  // 其他都是 truthy
}
```

## 🔍 詳細實現

### 編譯流程示例

#### 示例 1: `if (1 < 2) { 10 } else { 20 }`

**步驟 1: 編譯條件**
```
compile(1 < 2)
→ OpConstant 1 (索引 1, 值 2)
→ OpConstant 0 (索引 0, 值 1)
→ OpGreaterThan
```

**步驟 2: 發射條件跳轉**
```
emit(OpJumpNotTruthy, 9999)
→ 位置: 7
→ 佔位符: 9999
```

**步驟 3: 編譯 consequence**
```
compile(10)
→ OpConstant 2 (索引 2, 值 10)
移除 OpPop
```

**步驟 4: 發射無條件跳轉**
```
emit(OpJump, 9999)
→ 位置: 10
```

**步驟 5: 回填條件跳轉**
```
afterConsequence = 13
changeOperand(7, 13)
```

**步驟 6: 編譯 alternative**
```
compile(20)
→ OpConstant 3 (索引 3, 值 20)
移除 OpPop
```

**步驟 7: 回填無條件跳轉**
```
afterAlternative = 16
changeOperand(10, 16)
```

**最終字節碼:**
```
Constants: [1, 2, 10, 20]

Instructions:
0000 OpConstant 1      ; 2
0003 OpConstant 0      ; 1
0006 OpGreaterThan     ; 2 > 1 = true
0007 OpJumpNotTruthy 13
0010 OpConstant 2      ; 10
0013 OpJump 16
0016 OpConstant 3      ; 20
```

**執行:**
```
stack: []
→ OpConstant 1  → stack: [2]
→ OpConstant 0  → stack: [2, 1]
→ OpGreaterThan → stack: [true]
→ OpJumpNotTruthy 13
   ↓ true 是 truthy,不跳轉
→ OpConstant 2  → stack: [10]
→ OpJump 16     → 跳到結束
結果: 10
```

#### 示例 2: `if (false) { 10 }`

**編譯:**
```
Constants: [10]

Instructions:
0000 OpFalse
0001 OpJumpNotTruthy 7
0004 OpConstant 0  ; 10
0007 OpNull        ; 沒有 else
```

**執行:**
```
stack: []
→ OpFalse            → stack: [false]
→ OpJumpNotTruthy 7  → false 是 falsy,跳轉!
→ OpNull             → stack: [null]
結果: null
```

### EmittedInstruction 的作用

**問題:** 如何追蹤最後發射的指令?

**解決:** 使用 EmittedInstruction 記錄操作碼和位置

```java
public class EmittedInstruction {
    private final Opcode opcode;
    private final int position;
}

// Compiler 中維護
private EmittedInstruction lastInstruction;
private EmittedInstruction previousInstruction;

private void setLastInstruction(Opcode op, int pos) {
    previousInstruction = lastInstruction;
    lastInstruction = new EmittedInstruction(op, pos);
}
```

**使用場景:**
1. 檢查最後一條指令是否是 OpPop
2. 移除最後一條指令時恢復狀態

## 📊 新增操作碼總覽

| 操作碼 | 操作數 | 功能 | 堆疊變化 |
|--------|--------|------|----------|
| OpJumpNotTruthy | 2字節地址 | 條件跳轉 | [cond] → [] |
| OpJump | 2字節地址 | 無條件跳轉 | 無變化 |
| OpNull | 無 | 推入null | [] → [null] |

**總計:** Chapter 4 後共 16 個操作碼

## 🎓 重要概念

### 1. 控制流

編譯器通過跳轉指令實現控制流:
- **順序執行:** 不需要特殊處理
- **條件執行:** OpJumpNotTruthy
- **跳過代碼:** OpJump

### 2. 表達式 vs 語句

**if 在 Monkey 中是表達式:**
```monkey
let x = if (true) { 10 } else { 20 };
// x = 10
```

這意味著:
- if 必須有返回值
- 無 else 分支時返回 null
- 需要留值在堆疊上

### 3. 兩次編譯問題

**挑戰:** 編譯器需要知道跳轉目標,但目標在編譯時還不存在

**解決方案:**
1. **兩次編譯** - 第一次收集信息,第二次生成代碼 (複雜)
2. **回填** - 先用佔位符,後續修改 (簡單,我們的選擇)

## 💡 設計決策

### 1. 為什麼需要移除 OpPop?

```monkey
if (true) { 10 }
```

如果不移除 OpPop:
```
OpTrue
OpJumpNotTruthy ...
OpConstant 0   ; 10
OpPop          ; 10 被彈出了!
OpNull
```

堆疊上會留下 null 而不是 10。

移除後:
```
OpTrue
OpJumpNotTruthy ...
OpConstant 0   ; 10 留在堆疊上
OpNull
```

### 2. 為什麼用 9999 作為佔位符?

- 明顯的"錯誤"值
- 如果忘記回填,容易發現
- 不會與真實地址混淆

### 3. 為什麼 null 是單例?

```java
public static final NullObject NULL = new NullObject();
```

- 只需要一個 null 對象
- 可以用 == 比較
- 節省記憶體

## 🧪 測試要點

### 編譯器測試

驗證生成的字節碼結構:
```java
new CompilerTestCase(
    "if (true) { 10 }; 3333;",
    new Object[]{10, 3333},  // 常量池
    new byte[][]{
        Instructions.make(Opcode.OP_TRUE),
        Instructions.make(Opcode.OP_JUMP_NOT_TRUTHY, 10),
        Instructions.make(Opcode.OP_CONSTANT, 0),
        Instructions.make(Opcode.OP_JUMP, 11),
        Instructions.make(Opcode.OP_NULL),
        Instructions.make(Opcode.OP_POP),
        Instructions.make(Opcode.OP_CONSTANT, 1),
        Instructions.make(Opcode.OP_POP)
    }
);
```

### 虛擬機測試

驗證執行結果:
```java
new VMTestCase("if (true) { 10 }", 10),
new VMTestCase("if (false) { 10 }", VM.NULL),
new VMTestCase("if (1 < 2) { 10 } else { 20 }", 10),
new VMTestCase("if (1 > 2) { 10 } else { 20 }", 20)
```

## 🎉 完成第四章!

你現在擁有:

✅ **跳轉指令** - OpJump, OpJumpNotTruthy  
✅ **條件語句** - if/else 表達式  
✅ **回填技術** - 處理未知地址  
✅ **Null 值** - OpNull  
✅ **OpPop 優化** - 移除不必要的指令  
✅ **控制流** - 完整的條件執行

## 📚 下一步: Chapter 5

第五章將添加:

- **全局變數** - let 語句
- **符號表** - 追蹤變數
- **OpSetGlobal, OpGetGlobal** - 全局變數指令
- **變數作用域** - 名稱解析

## 🔧 常見問題

### Q: 為什麼 if 需要是表達式?

A: Monkey 設計為"表達式導向"語言,一切都有值。這使得代碼更簡潔:
```monkey
let x = if (condition) { 10 } else { 20 };
```

### Q: 為什麼 0 是 truthy?

A: 這是 Monkey 的設計選擇。不同語言有不同規則:
- JavaScript: 0 是 falsy
- Python: 0 是 falsy
- Ruby: 0 是 truthy
- **Monkey: 0 是 truthy**

### Q: 回填會影響性能嗎?

A: 不會。回填發生在編譯時,不影響運行時性能。而且我們只修改少數指令。

### Q: 可以嵌套 if 嗎?

A: 可以!編譯器遞歸處理:
```monkey
if (1 < 2) {
    if (2 < 3) {
        10
    }
}
```

# Writing A Compiler In Java - Chapter 5: Keeping Track of Names

## 🎯 本章目標

在第五章中,我們添加了變量支持:

1. ✅ **符號表** - 追蹤變量名稱和索引
2. ✅ **全局變量指令** - OpSetGlobal, OpGetGlobal
3. ✅ **let 語句編譯** - 變量定義
4. ✅ **標識符編譯** - 變量引用
5. ✅ **全局變量存儲** - VM 中的全局變量數組

## 📁 新增和修改的文件

### 核心文件 (修改)
- `Opcode.java` - 添加 OpSetGlobal, OpGetGlobal
- `Instructions.java` - 更新操作碼定義
- `Compiler.java` - 添加 let 語句和標識符編譯
- `VM.java` - 添加全局變量存儲和指令執行

### 新增類
- `Symbol.java` - 符號定義
- `SymbolScope.java` - 符號作用域枚舉
- `SymbolTable.java` - 符號表實現
- `LetStatement.java` - let 語句 AST 節點
- `Identifier.java` - 標識符 AST 節點

### 測試文件 (新增/更新)
- `SymbolTableTest.java` - 符號表測試
- `CompilerTest.java` - 添加 let 語句測試
- `VMTest.java` - 添加全局變量測試

## 🚀 快速開始

### 1. 編譯和測試

```bash
# 編譯
mvn clean compile

# 運行測試
mvn test

# 應該看到所有測試通過
[INFO] Tests run: 3, Failures: 0 (SymbolTableTest)
[INFO] Tests run: 4, Failures: 0 (CompilerTest)
[INFO] Tests run: 4, Failures: 0 (VMTest)
```

### 2. 使用 REPL

```bash
mvn exec:java -Dexec.mainClass="com.monkey.Main"
```

測試全局變量:

```
>> let x = 10
>> x
10
>> let y = 20
>> x + y
30
>> let z = x + y
>> z
30
```

## 📚 核心概念

### 1. 符號表

符號表是編譯器用來追蹤變量的數據結構。

**作用:**
- 記錄變量名稱
- 分配唯一索引
- 區分不同作用域

**結構:**
```java
public class SymbolTable {
    private Map<String, Symbol> store;  // 名稱 → 符號
    private int numDefinitions;         // 已定義數量
    
    public Symbol define(String name) {
        Symbol symbol = new Symbol(name, GLOBAL, numDefinitions);
        store.put(name, symbol);
        numDefinitions++;
        return symbol;
    }
    
    public Symbol resolve(String name) {
        return store.get(name);
    }
}
```

**Symbol 結構:**
```java
public class Symbol {
    private String name;         // 變量名
    private SymbolScope scope;   // 作用域
    private int index;           // 索引
}
```

### 2. 全局變量指令

#### OpSetGlobal - 設置全局變量

```
OpSetGlobal <index>
```

從堆疊彈出值並存儲到全局變量數組。

**示例:**
```monkey
let x = 10;
```

編譯為:
```
0000 OpConstant 0    ; 10
0003 OpSetGlobal 0   ; globals[0] = 10
```

執行:
```
stack: []
→ OpConstant 0  → stack: [10]
→ OpSetGlobal 0 → stack: [], globals[0] = 10
```

#### OpGetGlobal - 獲取全局變量

```
OpGetGlobal <index>
```

從全局變量數組載入值並推入堆疊。

**示例:**
```monkey
x
```

編譯為:
```
0000 OpGetGlobal 0   ; push globals[0]
0003 OpPop
```

執行:
```
stack: []
→ OpGetGlobal 0 → stack: [10]  (假設 globals[0] = 10)
→ OpPop         → stack: []
```

### 3. let 語句編譯

#### 編譯流程

```monkey
let x = 5 + 5;
```

**步驟:**

1. **編譯值表達式**
   ```
   compile(5 + 5)
   → OpConstant 0    ; 5
   → OpConstant 1    ; 5
   → OpAdd
   ```

2. **在符號表中定義變量**
   ```java
   Symbol symbol = symbolTable.define("x");
   // symbol = Symbol{name='x', scope=GLOBAL, index=0}
   ```

3. **發射 OpSetGlobal 指令**
   ```
   → OpSetGlobal 0
   ```

**完整字節碼:**
```
0000 OpConstant 0    ; 5
0003 OpConstant 1    ; 5
0006 OpAdd           ; 10
0007 OpSetGlobal 0   ; globals[0] = 10
```

### 4. 標識符編譯

#### 編譯流程

```monkey
x
```

**步驟:**

1. **在符號表中查找變量**
   ```java
   Symbol symbol = symbolTable.resolve("x");
   if (symbol == null) {
       throw new CompilerException("undefined variable x");
   }
   ```

2. **發射 OpGetGlobal 指令**
   ```java
   emit(Opcode.OP_GET_GLOBAL, symbol.getIndex());
   ```

**字節碼:**
```
0000 OpGetGlobal 0
0003 OpPop
```

### 5. 全局變量存儲

VM 使用數組存儲全局變量:

```java
private static final int GLOBALS_SIZE = 65536;
private final MonkeyObject[] globals;
```

**特點:**
- 固定大小: 65536 個槽位
- 索引: 0-65535
- 與 OpConstant 類似,操作數是 2 字節

**執行:**
```java
case OP_SET_GLOBAL:
    int index = readUint16(...);
    globals[index] = pop();
    break;

case OP_GET_GLOBAL:
    int index = readUint16(...);
    push(globals[index]);
    break;
```

## 🔍 詳細實現

### 編譯流程示例

#### 示例 1: `let x = 1; let y = 2; x + y`

**編譯:**

```
// let x = 1
OpConstant 0      ; 1
OpSetGlobal 0     ; globals[0] = 1

// let y = 2
OpConstant 1      ; 2
OpSetGlobal 1     ; globals[1] = 2

// x + y
OpGetGlobal 0     ; load globals[0]
OpGetGlobal 1     ; load globals[1]
OpAdd
OpPop
```

**符號表狀態:**
```
{
  "x" → Symbol{name='x', scope=GLOBAL, index=0},
  "y" → Symbol{name='y', scope=GLOBAL, index=1}
}
```

**執行:**
```
globals: [null, null, ...]

OpConstant 0      → stack: [1]
OpSetGlobal 0     → stack: [], globals: [1, null, ...]

OpConstant 1      → stack: [2]
OpSetGlobal 1     → stack: [], globals: [1, 2, ...]

OpGetGlobal 0     → stack: [1]
OpGetGlobal 1     → stack: [1, 2]
OpAdd             → stack: [3]
OpPop             → stack: []
```

#### 示例 2: `let one = 1; let two = one; two`

**編譯:**

```
// let one = 1
OpConstant 0      ; 1
OpSetGlobal 0     ; globals[0] = 1

// let two = one
OpGetGlobal 0     ; load one
OpSetGlobal 1     ; globals[1] = one

// two
OpGetGlobal 1     ; load two
OpPop
```

**符號表狀態:**
```
{
  "one" → Symbol{name='one', scope=GLOBAL, index=0},
  "two" → Symbol{name='two', scope=GLOBAL, index=1}
}
```

**執行:**
```
globals: [null, null, ...]

OpConstant 0      → stack: [1]
OpSetGlobal 0     → stack: [], globals: [1, null, ...]

OpGetGlobal 0     → stack: [1]
OpSetGlobal 1     → stack: [], globals: [1, 1, ...]

OpGetGlobal 1     → stack: [1]
OpPop             → stack: []
```

### 符號表實現

#### define() 方法

```java
public Symbol define(String name) {
    // Chapter 5: 所有變量都是全局的
    Symbol symbol = new Symbol(name, SymbolScope.GLOBAL, numDefinitions);
    store.put(name, symbol);
    numDefinitions++;
    return symbol;
}
```

**特點:**
- 自動遞增索引
- Chapter 5 中所有變量都是 GLOBAL 作用域
- Chapter 7 會添加 LOCAL 作用域

#### resolve() 方法

```java
public Symbol resolve(String name) {
    Symbol symbol = store.get(name);
    
    // 如果當前作用域找不到,嘗試在外層作用域查找
    if (symbol == null && outer != null) {
        return outer.resolve(name);
    }
    
    return symbol;
}
```

**特點:**
- Chapter 5 中 outer 始終為 null
- Chapter 7 會使用嵌套符號表

### REPL 中保持狀態

**問題:** 每次編譯都創建新的 VM,全局變量會丟失

```
>> let x = 10
>> x
undefined variable x  // 錯誤!
```

**解決:** 在多次編譯之間共享全局變量數組

```java
MonkeyObject[] globals = new MonkeyObject[VM.GLOBALS_SIZE];
SymbolTable symbolTable = new SymbolTable();

while (true) {
    String input = readLine();
    Program program = parse(input);
    
    Compiler compiler = new Compiler(symbolTable);  // 共享符號表
    compiler.compile(program);
    
    VM vm = new VM(compiler.bytecode(), globals);   // 共享 globals
    vm.run();
}
```

## 💡 設計決策

### 1. 為什麼用索引而不是名稱?

**使用索引:**
```
OpGetGlobal 0     ; 3 字節
```

**使用名稱:**
```
OpGetGlobal "x"   ; 1 + len("x") 字節
```

**優點:**
- ✅ 指令更短
- ✅ 執行更快 (數組訪問 vs 哈希查找)
- ✅ 固定長度指令

### 2. 為什麼全局變量數組大小是 65536?

因為操作數是 2 字節:
- 2 字節 = 16 位
- 2^16 = 65536

與 OpConstant 一致。

### 3. 為什麼需要符號表?

**沒有符號表:**
- 編譯器如何知道 `x` 的索引?
- 如何檢測未定義的變量?

**有符號表:**
```java
// 定義時
Symbol symbol = symbolTable.define("x");  // index = 0

// 使用時
Symbol symbol = symbolTable.resolve("x"); // 找到 index = 0
if (symbol == null) {
    throw new CompilerException("undefined variable");
}
```

### 4. 為什麼 OpSetGlobal 不推入值?

**當前行為:**
```monkey
let x = 10;
```
```
OpConstant 0    → stack: [10]
OpSetGlobal 0   → stack: []  (彈出值)
```

**如果推入值:**
```
OpConstant 0    → stack: [10]
OpSetGlobal 0   → stack: [10]  (保留值)
OpPop           → stack: []
```

多一條 OpPop 指令,沒有必要。

## 📊 新增操作碼總覽

| 操作碼 | 操作數 | 功能 | 堆疊變化 |
|--------|--------|------|----------|
| OpSetGlobal | 2字節索引 | 設置全局變量 | [val] → [] |
| OpGetGlobal | 2字節索引 | 獲取全局變量 | [] → [val] |

**總計:** Chapter 5 後共 18 個操作碼

## 🎓 重要概念

### 1. 編譯時 vs 運行時

**編譯時:**
- 符號表
- 名稱解析
- 索引分配

**運行時:**
- 全局變量數組
- 索引訪問
- 值存取

### 2. 名稱綁定

```monkey
let x = 10;  // 綁定 "x" 到值 10
```

**編譯器視角:**
- "x" → 索引 0
- 存儲映射關係

**VM 視角:**
- 索引 0 → 值 10
- 不知道名稱 "x"

### 3. 作用域

Chapter 5 中所有變量都是全局的:

```monkey
let x = 10;
if (true) {
    let y = 20;  // y 也是全局的
    x + y        // 可以訪問
}
y  // 可以訪問!
```

Chapter 7 會添加局部作用域。

## 🧪 測試要點

### 符號表測試

```java
@Test
public void testDefine() {
    SymbolTable global = new SymbolTable();
    
    Symbol a = global.define("a");
    assertEquals(0, a.getIndex());
    
    Symbol b = global.define("b");
    assertEquals(1, b.getIndex());
}

@Test
public void testResolveGlobal() {
    SymbolTable global = new SymbolTable();
    global.define("a");
    
    Symbol symbol = global.resolve("a");
    assertNotNull(symbol);
    assertEquals("a", symbol.getName());
}
```

### 編譯器測試

```java
new CompilerTestCase(
    "let one = 1; let two = 2;",
    new Object[]{1, 2},
    new byte[][]{
        Instructions.make(Opcode.OP_CONSTANT, 0),
        Instructions.make(Opcode.OP_SET_GLOBAL, 0),
        Instructions.make(Opcode.OP_CONSTANT, 1),
        Instructions.make(Opcode.OP_SET_GLOBAL, 1)
    }
);
```

### 虛擬機測試

```java
new VMTestCase("let one = 1; one", 1),
new VMTestCase("let one = 1; let two = 2; one + two", 3),
new VMTestCase("let one = 1; let two = one + one; one + two", 3)
```

## 🎉 完成第五章!

你現在擁有:

✅ **符號表** - 追蹤變量名稱和索引  
✅ **全局變量** - OpSetGlobal, OpGetGlobal  
✅ **let 語句** - 變量定義  
✅ **標識符** - 變量引用  
✅ **名稱解析** - 編譯時檢查未定義變量

## 📚 下一步: Chapter 6

第六章將添加:

- **字符串** - 字符串字面量
- **數組** - 數組字面量和索引
- **哈希表** - 哈希字面量和索引
- **OpArray, OpHash, OpIndex** - 新的數據結構指令

## 🔧 常見問題

### Q: 為什麼不直接存儲值,而要用索引?

A: 因為字節碼需要固定長度。索引是 2 字節,而值可能是任意大小(字符串、數組等)。

### Q: globals 數組會不會太大?

A: 不會。65536 個指針只占用約 512KB (64位系統)。而且大部分槽位是 null。

### Q: 可以重複定義變量嗎?

A: Chapter 5 中可以:
```monkey
let x = 1;
let x = 2;  // 會創建新的索引
```
這是設計缺陷,實際語言應該報錯。

### Q: 未使用的變量會怎樣?

A: 它們占用索引但不影響執行:
```monkey
let x = 1;
let y = 2;
x  // y 未使用但占用索引 1
```

## 📖 參考資料

- [原書: Writing A Compiler In Go](https://compilerbook.com/)
- [您的項目: monkey-java](https://github.com/Singularity-one/monkey-java)

---

**繼續加油!您的編譯器現在支持變量了!** 🚀


