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

# Writing A Compiler In Go — 第六章（Java 完整實現）

本專案提供《**Writing A Compiler In Go**》**第六章：String、Array、Hash** 的**完整 Java 實現**，涵蓋編譯器（Compiler）、虛擬機（VM）、位元組碼（Bytecode）與對象系統的擴展。

---

## 章節目標

本章為 Monkey 編譯器與虛擬機新增三種複合資料型別，並能正確編譯與執行以下程式：

```monkey
[1, 2, 3][1]
// => 2

{"one": 1, "two": 2, "three": 3}["o" + "ne"]
// => 1
```

---

## 新增語言特性

### 1. String（字串）

* 字串字面量作為常量
* 使用 `+` 進行字串連接
* 字串可作為 Hash 的鍵

### 2. Array（陣列）

* 陣列字面量：`[1, 2, 3]`
* 元素可為任意表達式
* 整數索引存取
* 支援巢狀陣列
* 越界存取回傳 `null`

### 3. Hash（雜湊表）

* 雜湊表字面量：`{key: value}`
* 鍵支援 `Integer / Boolean / String`
* 值可為任意表達式
* 鍵不存在回傳 `null`

### 4. Index Operator（索引運算子）

* 陣列索引：`array[index]`
* 雜湊表索引：`hash[key]`
* 支援巢狀索引與運算式索引

---

## 專案結構

```
project/
├── com/monkey/
│   ├── code/
│   │   ├── Opcode.java          # 操作碼列舉（新增 OP_ARRAY, OP_HASH, OP_INDEX）
│   │   ├── Instructions.java    # 位元組碼處理
│   │   └── Definition.java      # 操作碼定義
│   ├── object/
│   │   ├── ObjectType.java
│   │   ├── MonkeyObject.java
│   │   ├── IntegerObject.java   # 實作 Hashable
│   │   ├── BooleanObject.java   # 實作 Hashable
│   │   ├── StringObject.java    # ⭐ Chapter 6
│   │   ├── ArrayObject.java     # ⭐ Chapter 6
│   │   ├── HashObject.java      # ⭐ Chapter 6
│   │   ├── Hashable.java        # ⭐ Chapter 6
│   │   └── HashKey.java         # ⭐ Chapter 6
│   ├── compiler/
│   │   ├── Compiler.java        # 擴充字串、陣列、雜湊表
│   │   ├── CompilerTest.java
│   │   └── SymbolTable.java
│   └── vm/
│       ├── VM.java              # 擴充執行邏輯
│       └── VMTest.java
```

---

## 新增操作碼（Opcodes）

| Opcode     | 說明    | 操作數           |
| ---------- | ----- | ------------- |
| `OP_ARRAY` | 建立陣列  | 元素數量 (uint16) |
| `OP_HASH`  | 建立雜湊表 | 鍵值總數 (uint16) |
| `OP_INDEX` | 索引操作  | 無             |

---

## 編譯與執行流程概覽

### String 編譯與執行

* 編譯期：字串字面量加入常量池
* 執行期：`OpAdd` 對兩個 `StringObject` 進行連接

### Array 編譯與執行

* 依序編譯所有元素
* 使用 `OP_ARRAY` 建立陣列物件
* 索引越界回傳 `null`

### Hash 編譯與執行

* Key 排序以確保位元組碼穩定
* Key 必須實作 `Hashable`
* VM 建立 `HashKey → HashPair` 映射

### Index Operator

* 先彈出 index，再彈出被索引物件
* Array / Hash 分流處理
* 不支援型別直接拋出 VM 例外

---

## Hashable 設計

### 可作為 Hash Key 的型別

| 型別      | 支援 | 說明              |
| ------- | -- | --------------- |
| Integer | ✅  | 使用 long 值       |
| Boolean | ✅  | true=1, false=0 |
| String  | ✅  | 使用 `hashCode()` |
| Array   | ❌  | 不可雜湊            |
| Hash    | ❌  | 不可雜湊            |
| Null    | ❌  | 不可雜湊            |

---

## 測試

### 編譯器測試

```bash
./gradlew test --tests CompilerTest.testStringExpressions
./gradlew test --tests CompilerTest.testArrayLiterals
./gradlew test --tests CompilerTest.testHashLiterals
./gradlew test --tests CompilerTest.testIndexExpressions
```

### VM 測試

```bash
./gradlew test --tests VMTest.testStringExpressions
./gradlew test --tests VMTest.testArrayLiterals
./gradlew test --tests VMTest.testHashLiterals
./gradlew test --tests VMTest.testIndexExpressions
```

---

## 使用範例

### String

```monkey
"Hello" + " " + "World!"
// => "Hello World!"
```

### Array

```monkey
let arr = [1, 2, 3];
arr[1]
// => 2
```

### Hash

```monkey
let user = {"name": "Alice", "age": 30};
user["name"]
// => "Alice"
```

### 巢狀結構

```monkey
let data = [[1, 2], [3, 4]];
data[1][0]
// => 3
```

---

## 章節總結

第六章完成 Monkey 語言的**第一批複合資料型別**：

* String：可連接、可作為 Hash Key
* Array：支援索引與巢狀結構
* Hash：Key-Value 儲存與查詢
* Index Operator：統一索引語法

並新增三個關鍵 Opcode：

* `OP_ARRAY`
* `OP_HASH`
* `OP_INDEX`

---

## 下一章

**Chapter 7：Functions**

* 函數字面量
* 函數呼叫
* 區域變數
* 參數傳遞
* Return 指令

---

# Writing A Compiler In Go — 第七章（Java 完整實現）

本專案提供《**Writing A Compiler In Go**》**第七章：Functions（函數）** 的**完整 Java 實現**。本章是整個編譯器與虛擬機設計中最核心、也是最具挑戰性的一章，正式讓 Monkey 語言具備「可呼叫、可傳遞、可回傳」的函數系統。

---

## 章節目標

完成本章後，Monkey 語言可以正確編譯並執行以下程式：

```monkey
let sum = fn(a, b) {
    let c = a + b;
    c;
};

sum(1, 2) + sum(3, 4);
// => 10
```

---

## 本章新增語言特性

### 1. 函數字面量（Function Literal）

* 函數作為一級公民（First-class function）
* 函數可被指派、回傳、再次呼叫

### 2. 函數調用（Function Call）

* 支援任意數量參數
* 參數數量於執行期驗證

### 3. 局部變量（Local Bindings）

* 函數內的 `let` 綁定為局部變量
* 與全域變量完全隔離

### 4. 返回機制（Return）

* 顯式 `return <expr>`
* 隱式返回最後一個表達式
* 無返回值時回傳 `null`

### 5. 作用域管理（Scope）

* 全域作用域（GLOBAL）
* 函數作用域（LOCAL）
* 支援巢狀作用域

---

## 專案結構

```
project/
├── com/monkey/
│   ├── code/
│   │   ├── Opcode.java
│   │   ├── Instructions.java
│   │   └── Definition.java
│   ├── object/
│   │   ├── MonkeyObject.java
│   │   ├── ObjectType.java
│   │   └── CompiledFunctionObject.java   # ⭐ 編譯後函數
│   ├── compiler/
│   │   ├── Compiler.java
│   │   ├── CompilerTest.java
│   │   ├── SymbolTable.java
│   │   ├── Symbol.java
│   │   ├── SymbolScope.java               # ⭐ 新增 LOCAL
│   │   └── CompilationScope.java          # ⭐ 編譯作用域
│   └── vm/
│       ├── VM.java
│       ├── VMTest.java
│       └── Frame.java                     # ⭐ 調用幀
```

---

## 新增操作碼（Opcodes）

| Opcode            | 說明        | 操作數          |
| ----------------- | --------- | ------------ |
| `OP_CALL`         | 函數調用      | 參數數量 (uint8) |
| `OP_RETURN_VALUE` | 返回值       | 無            |
| `OP_RETURN`       | 返回 `null` | 無            |
| `OP_GET_LOCAL`    | 取得局部變量    | 局部索引         |
| `OP_SET_LOCAL`    | 設定局部變量    | 局部索引         |

---

## 核心新增元件

### CompiledFunctionObject

函數在**編譯期**會被轉換為 `CompiledFunctionObject` 並存入常量池：

* 獨立的指令序列（Instructions）
* 局部變量數量（numLocals）
* 參數數量（numParameters）

此設計讓函數成為可被 VM 呼叫的獨立單位。

---

### Frame（調用幀）

每一次函數呼叫都會建立一個新的 `Frame`：

* `ip`：指令指標
* `basePointer`：函數在 stack 中的起始位置
* `fn`：對應的 `CompiledFunctionObject`

Frame 負責隔離不同函數的執行狀態。

---

### CompilationScope（編譯作用域）

編譯器透過 **作用域堆疊** 管理巢狀函數：

* 每個函數擁有自己的指令序列
* 離開作用域時封裝為 `CompiledFunctionObject`
* 返回外層作用域繼續編譯

---

## 符號表（Symbol Table）擴展

### 新增 SymbolScope.LOCAL

```text
GLOBAL  → 全域變量
LOCAL   → 函數參數與局部變量
```

### 局部索引配置

```
fn(a, b) {
    let c = a + b;
}

符號索引：
  a → LOCAL 0
  b → LOCAL 1
  c → LOCAL 2
```

參數與局部變量共享同一索引空間。

---

## 函數編譯流程（摘要）

1. 進入新編譯作用域
2. 建立封閉的符號表
3. 定義參數為 LOCAL
4. 編譯函數主體
5. 處理隱式 / 顯式 return
6. 離開作用域並產生 CompiledFunction
7. 作為常量發射到主程式

---

## 函數調用流程（VM）

### Stack 佈局

呼叫前：

```
... | fn | arg1 | arg2 | <- sp
```

呼叫後：

```
... | fn | arg1 | arg2 | local1 | local2 | <- sp
           ^
           basePointer
```

### Return 行為

* `OP_RETURN_VALUE`：回傳表達式結果
* `OP_RETURN`：回傳 `null`
* 回傳後恢復前一個 Frame

---

## 使用範例

### 基本函數

```monkey
let add = fn(a, b) { a + b };
add(1, 2);
// => 3
```

### 局部變量

```monkey
let sum = fn(a, b) {
    let c = a + b;
    c;
};
```

### 函數回傳函數

```monkey
let returnsOne = fn() { 1; };
let f = fn() { returnsOne; };
f()();
// => 1
```

---

## 測試

### Compiler 測試

```bash
./gradlew test --tests CompilerTest.testFunctions
./gradlew test --tests CompilerTest.testFunctionCalls
./gradlew test --tests CompilerTest.testLetStatementScopes
```

### VM 測試

```bash
./gradlew test --tests VMTest.testCallingFunctionsWithoutArguments
./gradlew test --tests VMTest.testCallingFunctionsWithArgumentsAndBindings
```

---

## 與原書（Go 版）的差異

* Java 類別與介面取代 Go struct
* 使用例外處理（Exception）
* Frame 與 Stack 採用固定大小陣列
* 明確區分 GLOBAL / LOCAL 符號

---

## 章節總結

第七章讓 Monkey 語言正式成為**可寫實用程式的語言**：

* 函數是值
* 支援參數與回傳
* 正確的作用域與變量隔離
* 完整的呼叫堆疊模型

### 本章完成

* ✅ 函數字面量
* ✅ 函數調用
* ✅ 局部變量
* ✅ Return
* ✅ 作用域

### 尚未支援（後續章節）

* ❌ 內建函數（Chapter 8）
* ❌ 閉包 / 自由變量（Chapter 9）
* ❌ 遞歸（Chapter 9）

---

## 下一章

**Chapter 8：Built-in Functions**

* `len()` / `first()` / `last()`
* `rest()` / `push()`

---

# Writing A Compiler In Go - 第八章：Built-in Functions

本目錄包含《Writing A Compiler In Go》第八章的完整 Java 實現。

## 章節概述

第八章實現了內建函數系統，為 Monkey 語言添加了一組預定義的實用函數。這些函數直接內建在編譯器和虛擬機中，無需用戶定義即可使用。

**章節目標**：能夠編譯並執行以下 Monkey 代碼：
```monkey
len([1, 2, 3]);          // => 3
first([1, 2, 3]);        // => 1
last([1, 2, 3]);         // => 3
rest([1, 2, 3]);         // => [2, 3]
push([1, 2, 3], 4);      // => [1, 2, 3, 4]
puts("Hello World!");     // 打印 "Hello World!"
```

## 目錄結構
```
project/
├── com/monkey/
│   ├── code/
│   │   ├── Opcode.java          # 操作碼枚舉 (新增 OP_GET_BUILTIN)
│   │   └── Instructions.java    # 指令序列處理
│   ├── object/
│   │   ├── ObjectType.java      # 對象類型枚舉
│   │   ├── MonkeyObject.java    # 對象接口
│   │   ├── BuiltinObject.java   # ⭐ 內建函數對象 (第八章)
│   │   ├── Builtins.java        # ⭐ 內建函數定義 (第八章)
│   │   ├── ErrorObject.java     # ⭐ 錯誤對象 (第八章)
│   │   └── ...
│   ├── compiler/
│   │   ├── Compiler.java        # 編譯器 (擴展內建函數支持)
│   │   ├── CompilerTest.java    # 編譯器測試 (新增內建函數測試)
│   │   ├── SymbolTable.java     # 符號表 (新增 BUILTIN 作用域)
│   │   ├── SymbolScope.java     # 符號作用域 (新增 BUILTIN)
│   │   └── ...
│   └── vm/
│       ├── VM.java              # 虛擬機 (擴展內建函數執行)
│       └── VMTest.java          # 虛擬機測試 (新增內建函數測試)
```

## 第八章新增內容

### 1. 新增操作碼
```java
// code/Opcode.java
public enum Opcode {
    // ... 現有操作碼 ...
    
    // Chapter 8 - 內建函數
    OP_GET_BUILTIN((byte) 26);  // 獲取內建函數 (操作數: 內建函數索引)
}
```

**操作碼定義**：
```java
// code/Instructions.java
static {
    DEFINITIONS.put(Opcode.OP_GET_BUILTIN, new Definition("OpGetBuiltin", new int[]{1}));
}
```

- **操作數寬度**：1 字節（支持最多 256 個內建函數）
- **用途**：根據索引從 `Builtins.BUILTINS` 數組中載入內建函數

---

### 2. 內建函數對象 (BuiltinObject)
```java
public class BuiltinObject implements MonkeyObject {
    
    @FunctionalInterface
    public interface BuiltinFunction {
        MonkeyObject apply(MonkeyObject... args);
    }
    
    private final BuiltinFunction fn;

    @Override
    public ObjectType type() {
        return ObjectType.BUILTIN;
    }

    @Override
    public String inspect() {
        return "builtin function";
    }
}
```

**特性**：
- ✅ 使用 Java 函數式接口 `BuiltinFunction`
- ✅ 支持可變參數 `MonkeyObject... args`
- ✅ 返回 `MonkeyObject` 或 `null`（VM 會轉換為 NULL）

---

### 3. 內建函數定義 (Builtins.java)

所有內建函數都定義在 `Builtins.BUILTINS` 數組中：
```java
public static final BuiltinDefinition[] BUILTINS = new BuiltinDefinition[]{
    // 索引 0: len
    new BuiltinDefinition("len", new BuiltinObject(args -> { ... })),
    
    // 索引 1: puts
    new BuiltinDefinition("puts", new BuiltinObject(args -> { ... })),
    
    // 索引 2: first
    new BuiltinDefinition("first", new BuiltinObject(args -> { ... })),
    
    // 索引 3: last
    new BuiltinDefinition("last", new BuiltinObject(args -> { ... })),
    
    // 索引 4: rest
    new BuiltinDefinition("rest", new BuiltinObject(args -> { ... })),
    
    // 索引 5: push
    new BuiltinDefinition("push", new BuiltinObject(args -> { ... }))
};
```

**關鍵設計**：
- 📋 **數組索引**：決定 `OpGetBuiltin` 指令的操作數
- 📋 **穩定順序**：索引不能改變，保證編譯器和 VM 的一致性
- 📋 **統一訪問**：編譯器和 VM 都使用相同的 `Builtins.BUILTINS` 數組

---

### 4. BUILTIN 作用域

新增第三種符號作用域：
```java
// compiler/SymbolScope.java
public enum SymbolScope {
    GLOBAL("GLOBAL"),
    LOCAL("LOCAL"),
    BUILTIN("BUILTIN");  // ⭐ 第八章新增
}
```

**作用域特性**：
- 🌐 **GLOBAL**：全局變量（所有函數共享）
- 🏠 **LOCAL**：局部變量（函數內部）
- 🔧 **BUILTIN**：內建函數（預定義，不可修改）

---

### 5. 符號表擴展

新增 `defineBuiltin` 方法：
```java
public class SymbolTable {
    /**
     * Chapter 8: 定義內建函數
     */
    public Symbol defineBuiltin(int index, String name) {
        Symbol symbol = new Symbol(name, SymbolScope.BUILTIN, index);
        store.put(name, symbol);
        return symbol;
    }
}
```

**在編譯器初始化時定義所有內建函數**：
```java
public Compiler() {
    // ...
    
    // Chapter 8: 定義所有內建函數
    for (int i = 0; i < Builtins.BUILTINS.length; i++) {
        symbolTable.defineBuiltin(i, Builtins.BUILTINS[i].name);
    }
    
    // ...
}
```

---

## 內建函數詳解

### 1. len - 獲取長度

**功能**：返回字串或陣列的長度
```monkey
len("hello")      // => 5
len([1, 2, 3])    // => 3
len("")           // => 0
len([])           // => 0
```

**實現**：
```java
new BuiltinObject(args -> {
    if (args.length != 1) {
        return newError("wrong number of arguments. got=%d, want=1", args.length);
    }

    if (args[0] instanceof ArrayObject) {
        return new IntegerObject(((ArrayObject) args[0]).getElements().size());
    } else if (args[0] instanceof StringObject) {
        return new IntegerObject(((StringObject) args[0]).getValue().length());
    } else {
        return newError("argument to `len` not supported, got %s", args[0].type());
    }
})
```

**錯誤處理**：
- ❌ `len(1)` → "argument to `len` not supported, got INTEGER"
- ❌ `len("a", "b")` → "wrong number of arguments. got=2, want=1"

---

### 2. puts - 打印輸出

**功能**：打印任意數量的參數到標準輸出
```monkey
puts("Hello")              // 打印: Hello
puts("Hello", "World!")    // 打印: Hello
                           //      World!
```

**實現**：
```java
new BuiltinObject(args -> {
    for (MonkeyObject arg : args) {
        System.out.println(arg.inspect());
    }
    return null;  // 返回 null，VM 會轉換為 NULL
})
```

**特性**：
- ✅ 接受任意數量的參數
- ✅ 每個參數單獨一行
- ✅ 返回 `null`（顯示為 Monkey 的 `null`）

---

### 3. first - 獲取第一個元素

**功能**：返回陣列的第一個元素
```monkey
first([1, 2, 3])  // => 1
first([])         // => null
```

**實現**：
```java
new BuiltinObject(args -> {
    if (args.length != 1) {
        return newError("wrong number of arguments. got=%d, want=1", args.length);
    }
    if (!(args[0] instanceof ArrayObject)) {
        return newError("argument to `first` must be ARRAY, got %s", args[0].type());
    }

    ArrayObject arr = (ArrayObject) args[0];
    if (arr.getElements().size() > 0) {
        return arr.getElements().get(0);
    }
    return null;
})
```

**邊界處理**：
- 🔄 空陣列返回 `null`
- ❌ 非陣列參數返回錯誤

---

### 4. last - 獲取最後一個元素

**功能**：返回陣列的最後一個元素
```monkey
last([1, 2, 3])  // => 3
last([])         // => null
```

**實現**：
```java
new BuiltinObject(args -> {
    // ... 參數驗證 ...
    
    ArrayObject arr = (ArrayObject) args[0];
    int length = arr.getElements().size();
    if (length > 0) {
        return arr.getElements().get(length - 1);
    }
    return null;
})
```

---

### 5. rest - 獲取除第一個外的所有元素

**功能**：返回去掉第一個元素後的新陣列（不修改原陣列）
```monkey
rest([1, 2, 3])  // => [2, 3]
rest([1])        // => []
rest([])         // => null
```

**實現**：
```java
new BuiltinObject(args -> {
    // ... 參數驗證 ...
    
    ArrayObject arr = (ArrayObject) args[0];
    int length = arr.getElements().size();
    if (length > 0) {
        List<MonkeyObject> newElements = new ArrayList<>(
            arr.getElements().subList(1, length)
        );
        return new ArrayObject(newElements);
    }
    return null;
})
```

**特性**：
- ✅ 不修改原陣列（不可變性）
- ✅ 創建新的陣列對象
- ✅ 空陣列返回 `null`

---

### 6. push - 添加元素

**功能**：將元素添加到陣列末尾（返回新陣列）
```monkey
push([1, 2, 3], 4)  // => [1, 2, 3, 4]
push([], 1)         // => [1]
```

**實現**：
```java
new BuiltinObject(args -> {
    if (args.length != 2) {
        return newError("wrong number of arguments. got=%d, want=2", args.length);
    }
    if (!(args[0] instanceof ArrayObject)) {
        return newError("argument to `push` must be ARRAY, got %s", args[0].type());
    }

    ArrayObject arr = (ArrayObject) args[0];
    List<MonkeyObject> newElements = new ArrayList<>(arr.getElements());
    newElements.add(args[1]);
    return new ArrayObject(newElements);
})
```

**特性**：
- ✅ 不修改原陣列（不可變性）
- ✅ 可以添加任意類型的元素
- ✅ 創建新的陣列對象

---

## 編譯流程

### 示例：編譯內建函數調用

**輸入 Monkey 代碼**：
```monkey
len([1, 2, 3])
```

**編譯步驟**：

1. **解析調用表達式**
    - 函數：`Identifier("len")`
    - 參數：`ArrayLiteral([1, 2, 3])`

2. **解析標識符 "len"**
```java
   Symbol symbol = symbolTable.resolve("len");
   // symbol = Symbol{name="len", scope=BUILTIN, index=0}
```

3. **載入內建函數**
```java
   loadSymbol(symbol);
   // 根據 scope=BUILTIN，發射: OpGetBuiltin 0
```

4. **編譯參數**
```
   OpConstant 0    // 1
   OpConstant 1    // 2
   OpConstant 2    // 3
   OpArray 3       // [1, 2, 3]
```

5. **發射調用指令**
```
   OpCall 1        // 調用，1 個參數
```

**完整編譯結果**：
```
0000 OpGetBuiltin 0    // 載入 len 函數
0002 OpConstant 0      // 1
0005 OpConstant 1      // 2
0008 OpConstant 2      // 3
0011 OpArray 3         // [1, 2, 3]
0014 OpCall 1          // 調用 len([1, 2, 3])
0016 OpPop

常量池:
  0: 1
  1: 2
  2: 3
```

---

## VM 執行流程

### 示例：執行內建函數

**執行 `len([1, 2, 3])`**：
```
步驟 1: OpGetBuiltin 0
  - 從 Builtins.BUILTINS[0] 獲取 len 函數
  - push(BuiltinObject[len])
  - stack = [BuiltinObject]

步驟 2-4: 載入常量 1, 2, 3
  - stack = [BuiltinObject, 1, 2, 3]

步驟 5: OpArray 3
  - 創建陣列 [1, 2, 3]
  - stack = [BuiltinObject, ArrayObject[1,2,3]]

步驟 6: OpCall 1
  - callee = stack[sp-1-1] = BuiltinObject
  - 檢測到是內建函數
  - 調用 executeBuiltinFunction()
  
  executeBuiltinFunction:
    1. 收集參數: args = [ArrayObject[1,2,3]]
    2. 調用: builtin.getFn().apply(args)
    3. len 函數執行:
       - 檢查參數數量: 1 ✓
       - 檢查類型: ArrayObject ✓
       - 返回: IntegerObject(3)
    4. 調整堆疊: sp = sp - 1 - 1 = 0
    5. 推入結果: push(IntegerObject(3))
  
  - stack = [IntegerObject(3)]

步驟 7: OpPop
  - stack = []
  - lastPoppedStackElem = IntegerObject(3)
```

**關鍵點**：
- ✅ 內建函數不創建調用幀
- ✅ 直接在當前堆疊上執行
- ✅ 執行後清理堆疊（移除函數和參數）

---

## VM 中的內建函數處理

### executeCall 方法擴展
```java
private void executeCall(int numArgs) throws VMException {
    MonkeyObject callee = stack[sp - 1 - numArgs];
    
    // Chapter 8: 處理內建函數調用
    if (callee instanceof BuiltinObject) {
        executeBuiltinFunction((BuiltinObject) callee, numArgs);
        return;
    }
    
    // 處理普通函數...
}
```

### executeBuiltinFunction 方法
```java
private void executeBuiltinFunction(BuiltinObject builtin, int numArgs) 
        throws VMException {
    // 1. 收集參數
    MonkeyObject[] args = new MonkeyObject[numArgs];
    for (int i = 0; i < numArgs; i++) {
        args[i] = stack[sp - numArgs + i];
    }
    
    // 2. 調用內建函數
    MonkeyObject result = builtin.getFn().apply(args);
    
    // 3. 調整堆疊指針（移除函數和參數）
    sp = sp - numArgs - 1;
    
    // 4. 推入結果（null 轉換為 NULL）
    if (result != null) {
        push(result);
    } else {
        push(NULL);
    }
}
```

**堆疊變化示例**：
```
調用前: [..., BuiltinObject, arg1, arg2, arg3]
                                              ↑
                                              sp

收集參數: args = [arg1, arg2, arg3]

調用函數: result = builtin.apply(args)

調整 sp: sp = sp - 3 - 1 = sp - 4

調用後: [..., result]
                    ↑
                    sp
```

---

## 錯誤處理

### ErrorObject
```java
public class ErrorObject implements MonkeyObject {
    private final String message;

    @Override
    public ObjectType type() {
        return ObjectType.ERROR;
    }

    @Override
    public String inspect() {
        return "ERROR: " + message;
    }
}
```

### 錯誤創建
```java
private static ErrorObject newError(String format, Object... args) {
    return new ErrorObject(String.format(format, args));
}
```

### 錯誤示例
```monkey
len(1)
// => ERROR: argument to `len` not supported, got INTEGER

len("a", "b")
// => ERROR: wrong number of arguments. got=2, want=1

first(1)
// => ERROR: argument to `first` must be ARRAY, got INTEGER

push([], 1, 2)
// => ERROR: wrong number of arguments. got=3, want=2
```

---

## 測試

### 編譯器測試
```bash
# 運行所有編譯器測試
./gradlew test --tests CompilerTest

# 運行特定測試
./gradlew test --tests CompilerTest.testBuiltins
```

**測試內容**：
```java
@Test
public void testBuiltins() {
    CompilerTestCase[] tests = new CompilerTestCase[]{
            new CompilerTestCase(
                    "len([]); push([], 1);",
                    new Object[]{1},
                    new byte[][]{
                            Instructions.make(Opcode.OP_GET_BUILTIN, 0),  // len
                            Instructions.make(Opcode.OP_ARRAY, 0),
                            Instructions.make(Opcode.OP_CALL, 1),
                            Instructions.make(Opcode.OP_POP),
                            Instructions.make(Opcode.OP_GET_BUILTIN, 5),  // push
                            Instructions.make(Opcode.OP_ARRAY, 0),
                            Instructions.make(Opcode.OP_CONSTANT, 0),
                            Instructions.make(Opcode.OP_CALL, 2),
                            Instructions.make(Opcode.OP_POP)
                    }
            )
    };
    runCompilerTests(tests);
}
```

### 符號表測試
```bash
./gradlew test --tests SymbolTableTest.testDefineResolveBuiltins
```

**測試內容**：
- ✅ 在全局作用域定義內建函數
- ✅ 在嵌套作用域解析內建函數
- ✅ 內建函數在所有作用域都可訪問

### 虛擬機測試
```bash
./gradlew test --tests VMTest.testBuiltinFunctions
```

**測試覆蓋**：
- ✅ len 函數的各種用例
- ✅ puts 函數輸出
- ✅ first/last/rest/push 的邊界情況
- ✅ 錯誤處理（參數數量、類型檢查）

---

## 完整示例

### 示例 1：組合使用內建函數
```monkey
let map = fn(arr, f) {
    let iter = fn(arr, accumulated) {
        if (len(arr) == 0) {
            accumulated
        } else {
            iter(rest(arr), push(accumulated, f(first(arr))));
        }
    };
    iter(arr, []);
};

let a = [1, 2, 3, 4];
let double = fn(x) { x * 2 };
map(a, double);
// => [2, 4, 6, 8]
```

**執行流程**：
1. 定義 `map` 函數（高階函數）
2. 定義 `iter` 遞歸函數（注意：需要第九章的閉包支持）
3. 使用 `len`, `rest`, `push`, `first` 實現映射

### 示例 2：實現 reduce
```monkey
let reduce = fn(arr, initial, f) {
    let iter = fn(arr, result) {
        if (len(arr) == 0) {
            result
        } else {
            iter(rest(arr), f(result, first(arr)));
        }
    };
    iter(arr, initial);
};

let sum = fn(arr) {
    reduce(arr, 0, fn(initial, el) { initial + el });
};

sum([1, 2, 3, 4, 5]);
// => 15
```

### 示例 3：字串處理
```monkey
let greeting = "Hello, World!";
puts("Length:", len(greeting));
// 打印: Length:
//      13

let words = ["Hello", "World"];
let join = fn(arr, sep) {
    let iter = fn(arr, result) {
        if (len(arr) == 0) {
            result
        } else {
            let newResult = if (len(result) == 0) {
                first(arr)
            } else {
                result + sep + first(arr)
            };
            iter(rest(arr), newResult);
        }
    };
    iter(arr, "");
};

join(words, ", ");
// => "Hello, World"
```

---

## 架構設計

### 內建函數的三個組成部分
```
┌─────────────────────────────────────────┐
│         Builtins.BUILTINS               │
│  (object 包中的統一定義)                 │
│                                         │
│  [0] len    - 獲取長度                   │
│  [1] puts   - 打印輸出                   │
│  [2] first  - 第一個元素                 │
│  [3] last   - 最後一個元素               │
│  [4] rest   - 除第一個外的所有元素        │
│  [5] push   - 添加元素                   │
└─────────────────────────────────────────┘
         ↑                    ↑
         │                    │
    編譯時使用            運行時使用
         │                    │
┌────────┴────────┐   ┌───────┴────────┐
│   Compiler      │   │      VM        │
│                 │   │                │
│ 1. 初始化時定義  │   │ 1. OpGetBuiltin│
│    所有內建函數  │   │    載入函數     │
│                 │   │                │
│ 2. 解析標識符   │   │ 2. OpCall      │
│    → BUILTIN    │   │    執行函數     │
│      作用域     │   │                │
│                 │   │ 3. 推入結果     │
│ 3. 發射        │   │    (或 NULL)   │
│    OpGetBuiltin │   │                │
└─────────────────┘   └────────────────┘
```

### 調用約定統一性

內建函數和普通函數使用相同的調用約定：
```
普通函數調用:
  OpGetGlobal 0     // 載入函數
  OpConstant 1      // 參數 1
  OpConstant 2      // 參數 2
  OpCall 2          // 調用

內建函數調用:
  OpGetBuiltin 0    // 載入內建函數
  OpConstant 1      // 參數 1
  OpConstant 2      // 參數 2
  OpCall 2          // 調用 (相同!)
```

**優勢**：
- ✅ 統一的調用方式
- ✅ 編譯器邏輯簡化
- ✅ 易於擴展新的內建函數

---

## 與原書的差異

### 語言特性
- ✅ **Java 函數式接口**：使用 `@FunctionalInterface` 定義內建函數
- ✅ **Lambda 表達式**：內建函數定義使用 lambda
- ✅ **錯誤處理**：使用 `ErrorObject` 而非字串

### 設計模式
- ✅ **數組而非切片**：`BuiltinDefinition[]` vs Go 的 `[]struct`
- ✅ **靜態定義**：使用 `static final` 確保常量
- ✅ **函數式風格**：充分利用 Java 8+ 的函數式特性

### 命名慣例
- ✅ **駝峰命名**：`getBuiltinByName()` vs Go 的 `GetBuiltinByName`
- ✅ **類命名**：`BuiltinObject` vs Go 的 `object.Builtin`
- ✅ **包結構**：`com.monkey.object` vs Go 的 `monkey/object`

---

## 性能考量

### 1. 內建函數查找

- **編譯時**：O(1) - 直接使用數組索引
- **運行時**：O(1) - 直接訪問 `Builtins.BUILTINS[index]`

### 2. 不可變數據結構

所有內建函數都遵循不可變性原則：
```java
// push 不修改原陣列
List<MonkeyObject> newElements = new ArrayList<>(arr.getElements());
newElements.add(args[1]);
return new ArrayObject(newElements);

// rest 創建新的子列表
List<MonkeyObject> newElements = new ArrayList<>(
    arr.getElements().subList(1, length)
);
```

**優勢**：
- ✅ 線程安全
- ✅ 避免意外修改
- ✅ 函數式編程風格

### 3. 錯誤處理開銷

- 使用對象而非異常（避免異常開銷）
- 錯誤作為正常返回值處理
- VM 不需要特殊的錯誤處理邏輯

---

## 常見問題

### Q1: 為什麼內建函數不創建調用幀？

A: 內建函數是用 Java 實現的，不需要執行 Monkey 字節碼。它們直接在當前堆疊上操作，執行效率更高。

### Q2: 為什麼內建函數返回 null 而不是 NULL？

A: `null` 是 Java 的值，VM 在接收到 `null` 時會自動轉換為 Monkey 的 `NULL` 對象。這樣內建函數的實現更簡潔。
```java
// 內建函數返回
return null;

// VM 處理
if (result != null) {
    push(result);
} else {
    push(NULL);  // 轉換為 Monkey 的 NULL
}
```

### Q3: 如何添加新的內建函數？

步驟：
1. 在 `Builtins.BUILTINS` 數組末尾添加新函數定義
2. 實現函數邏輯
3. 無需修改編譯器或 VM 代碼（自動支持）
```java
// 添加新的內建函數
new BuiltinDefinition("max", new BuiltinObject(args -> {
    // 實現邏輯...
}))
```

### Q4: 為什麼使用數組而不是 Map？

A: 數組提供：
- ✅ **穩定的索引**：索引不會改變
- ✅ **O(1) 訪問**：直接通過索引訪問
- ✅ **順序保證**：迭代順序穩定
- ✅ **編譯時確定**：索引在編譯時確定

### Q5: 內建函數可以調用其他內建函數嗎？

A: 可以，但不推薦。最好在 Monkey 代碼中組合使用：
```monkey
// 好的做法：在 Monkey 中組合
let second = fn(arr) { first(rest(arr)) };

// 避免：在 Java 中組合內建函數
// 這樣會增加複雜度
```

---

## 擴展建議

### 可以添加的內建函數

1. **字串操作**：
```monkey
   split("a,b,c", ",")    // => ["a", "b", "c"]
   join(["a", "b"], ",")  // => "a,b"
```

2. **數學函數**：
```monkey
   max([1, 5, 3])         // => 5
   min([1, 5, 3])         // => 1
   sum([1, 2, 3])         // => 6
```

3. **類型檢查**：
```monkey
   type(5)                // => "INTEGER"
   isArray([1, 2])        // => true
```

4. **文件 I/O**（高級）：
```monkey
   read("file.txt")       // => "file contents"
   write("file.txt", "data")
```

---

## 章節總結

第八章實現了內建函數系統：

### 新增組件
1. **BuiltinObject** - 內建函數對象
2. **Builtins** - 統一的內建函數定義
3. **ErrorObject** - 錯誤對象
4. **BUILTIN 作用域** - 第三種符號作用域

### 新增操作碼
- `OP_GET_BUILTIN` - 載入內建函數

### 實現的內建函數
- ✅ `len` - 獲取長度
- ✅ `puts` - 打印輸出
- ✅ `first` - 獲取第一個元素
- ✅ `last` - 獲取最後一個元素
- ✅ `rest` - 獲取除第一個外的所有元素
- ✅ `push` - 添加元素到陣列

### 關鍵設計決策
- ✅ 統一的調用約定（內建函數和普通函數相同）
- ✅ 不可變數據結構（函數式風格）
- ✅ 集中式定義（`Builtins.BUILTINS` 數組）
- ✅ 作用域隔離（BUILTIN 作用域）

---

## 下一章預告

第九章將實現**閉包 (Closures)**：
- 自由變量捕獲
- 遞歸函數支持
- 高階函數完整實現
- 函數可以訪問外層函數的局部變量

有了閉包，我們就可以實現：
```monkey
let newAdder = fn(a) {
    fn(b) { a + b };  // 閉包：捕獲外層的 a
};
let addTwo = newAdder(2);
addTwo(3);  // => 5
```

---

# Writing A Compiler In Go - 第九章：Closures (閉包)

本目錄包含《Writing A Compiler In Go》第九章的完整 Java 實現。

## 章節概述

第九章實現了閉包系統，這是編譯器和虛擬機中最重要且複雜的特性之一。閉包允許函數"捕獲"並攜帶定義時所在作用域的變量，即使在函數定義的作用域已經結束後，這些變量仍然可以被訪問。

**章節目標**：能夠編譯並執行以下 Monkey 代碼：
```monkey
let newAdder = fn(a) {
    fn(b) { a + b };
};
let addTwo = newAdder(2);
addTwo(3);  // => 5
```

## 核心概念

### 什麼是閉包？

閉包（Closure）是一個函數及其引用的外部變量的組合。當一個函數引用了外層函數的變量時，即使外層函數已經返回，內層函數仍然可以訪問這些變量。

**示例**：
```monkey
let newAdder = fn(a) {
    let adder = fn(b) { a + b; };
    return adder;
};

let addTwo = newAdder(2);
addTwo(3);  // => 5
```

在這個例子中：
- `adder` 函數是一個閉包
- 它"捕獲"了外層函數的參數 `a`
- 即使 `newAdder` 已經返回，`adder` 仍然可以訪問 `a` 的值（2）

### 關鍵術語

#### 自由變量 (Free Variables)

**定義**：從當前函數的角度看，既不是當前函數的參數，也不是當前函數內部定義的局部變量，而是來自外層作用域的變量。

**示例**：
```monkey
fn(a) {          // a 是參數
    let b = 1;   // b 是局部變量
    fn(c) {      // c 是參數
        a + b + c;  // a 和 b 是自由變量（對內層函數而言）
    }
}
```

從內層函數的角度：
- `c` - 局部參數
- `a`, `b` - 自由變量（來自外層作用域）

#### 為什麼叫"自由"變量？

因為這些變量不受當前作用域的約束（not bound to the current scope），它們"自由地"來自外層作用域。

### 實現策略

我們採用的策略是：**將每個函數都視為閉包**

即使函數不引用任何自由變量，我們也將其包裝為閉包。這簡化了編譯器和 VM 的架構，減少了特殊情況的處理。

**優點**：
- ✅ 統一的調用約定
- ✅ 簡化編譯器邏輯
- ✅ 減少 VM 中的條件判斷

**代價**：
- 輕微的性能開銷（可通過後續優化消除）

---

## 目錄結構
```
project/
├── com/monkey/
│   ├── code/
│   │   ├── Opcode.java          # 操作碼枚舉 (新增 OP_CLOSURE, OP_GET_FREE)
│   │   └── Instructions.java    # 指令序列處理 (支持雙操作數)
│   ├── object/
│   │   ├── ObjectType.java      # 對象類型枚舉 (新增 CLOSURE)
│   │   ├── ClosureObject.java   # ⭐ 閉包對象 (第九章核心)
│   │   └── ...
│   ├── compiler/
│   │   ├── Compiler.java        # 編譯器 (擴展閉包支持)
│   │   ├── CompilerTest.java    # 編譯器測試 (新增閉包測試)
│   │   ├── SymbolTable.java     # 符號表 (新增 FREE 作用域和自由變量追蹤)
│   │   ├── SymbolTableTest.java # 符號表測試 (測試自由變量解析)
│   │   ├── SymbolScope.java     # 符號作用域 (新增 FREE)
│   │   └── ...
│   └── vm/
│       ├── VM.java              # 虛擬機 (擴展閉包執行)
│       ├── VMTest.java          # 虛擬機測試 (新增閉包測試)
│       ├── Frame.java           # 調用幀 (改為存儲閉包而非函數)
│       └── ...
```

---

## 第九章新增內容

### 1. 新增對象類型

#### ClosureObject
```java
public class ClosureObject implements MonkeyObject {
    private final CompiledFunctionObject fn;
    private final MonkeyObject[] free;  // 自由變量數組

    public ClosureObject(CompiledFunctionObject fn, MonkeyObject[] free) {
        this.fn = fn;
        this.free = free;
    }
    
    @Override
    public ObjectType type() {
        return ObjectType.CLOSURE;
    }
}
```

**組成部分**：
- `fn` - 被包裝的編譯後的函數
- `free` - 自由變量數組（運行時創建）

**關鍵特性**：
- 包裝編譯後的函數 (`CompiledFunctionObject`)
- 攜帶自由變量數組（按索引訪問）
- 可以像普通函數一樣被調用
- 自由變量在閉包創建時從堆疊複製

---

### 2. 新增操作碼

#### OP_CLOSURE
```java
OP_CLOSURE((byte) 27)
```

**操作數**：
- 第 1 個操作數（2 bytes）：函數在常量池中的索引
- 第 2 個操作數（1 byte）：自由變量的數量

**功能**：創建閉包
1. 從常量池獲取編譯後的函數
2. 從堆疊收集指定數量的自由變量
3. 創建閉包對象
4. 推入堆疊

**示例**：
```
OpClosure 0 2
```
- 從常量池獲取索引 0 的函數
- 從堆疊取 2 個自由變量
- 創建閉包並推入堆疊

**編譯結果示例**：
```monkey
fn(a) {
    fn(b) { a + b }
}
```

編譯為：
```
外層函數:
  OpGetLocal 0      // 載入 a
  OpClosure 0 1     // 創建內層閉包，1 個自由變量
  OpReturnValue

主程序:
  OpClosure 1 0     // 創建外層閉包，0 個自由變量
  OpPop
```

#### OP_GET_FREE
```java
OP_GET_FREE((byte) 28)
```

**操作數**：
- 1 byte：自由變量在 free 數組中的索引

**功能**：獲取自由變量
1. 從當前閉包的 free 數組中獲取指定索引的變量
2. 推入堆疊

**示例**：
```
OpGetFree 0  // 獲取第 0 個自由變量
OpGetFree 1  // 獲取第 1 個自由變量
```

**使用示例**：
```monkey
fn(a) {
    fn(b) {
        a + b  // a 是自由變量
    }
}
```

內層函數編譯為：
```
OpGetFree 0     // 獲取 a (自由變量)
OpGetLocal 0    // 獲取 b (局部參數)
OpAdd
OpReturnValue
```

---

### 3. 新增符號作用域
```java
public enum SymbolScope {
    GLOBAL("GLOBAL"),   // 全局變量
    LOCAL("LOCAL"),     // 局部變量
    BUILTIN("BUILTIN"), // 內建函數
    FREE("FREE");       // ⭐ 自由變量 (第九章新增)
}
```

**FREE 作用域的作用**：
- 標識來自外層作用域的變量
- 觸發 `OpGetFree` 指令的發射
- 區別於 `LOCAL` 和 `GLOBAL` 的訪問方式

**作用域對應的指令**：
| 作用域 | 訪問指令 | 來源 |
|--------|----------|------|
| GLOBAL | OpGetGlobal | 全局存儲 |
| LOCAL | OpGetLocal | 當前堆疊幀 |
| BUILTIN | OpGetBuiltin | 內建函數表 |
| FREE | OpGetFree | 當前閉包的 free 數組 |

---

### 4. 符號表擴展

#### 新增字段
```java
public class SymbolTable {
    private final SymbolTable outer;
    private final Map<String, Symbol> store;
    private int numDefinitions;
    
    // ⭐ Chapter 9: 自由變量列表
    private final List<Symbol> freeSymbols;
}
```

**freeSymbols 的作用**：
- 記錄所有被識別為自由變量的符號
- 保存原始符號（來自外層作用域）
- 用於在離開作用域後載入自由變量

#### 新增方法：defineFree
```java
public Symbol defineFree(Symbol original) {
    freeSymbols.add(original);
    
    Symbol symbol = new Symbol(
        original.getName(), 
        SymbolScope.FREE, 
        freeSymbols.size() - 1  // 在 free 數組中的索引
    );
    store.put(original.getName(), symbol);
    
    return symbol;
}
```

**功能**：
1. 將原始符號添加到 `freeSymbols` 列表
2. 創建新的 FREE 作用域符號（索引為在 free 數組中的位置）
3. 存儲到當前符號表
4. 返回新符號

**為什麼保存原始符號？**

因為自由變量的"身份"是相對的：
- 在當前作用域：它是 FREE 變量
- 在外層作用域：它可能是 LOCAL 變量或另一個 FREE 變量

我們需要原始符號來知道如何在外層作用域載入它。

#### 更新方法：resolve
```java
public Symbol resolve(String name) {
    Symbol symbol = store.get(name);
    
    if (symbol == null && outer != null) {
        symbol = outer.resolve(name);
        
        if (symbol == null) {
            return null;
        }
        
        // ⭐ 關鍵邏輯
        // 全局變量和內建函數直接返回（無需作為自由變量）
        if (symbol.getScope() == SymbolScope.GLOBAL || 
            symbol.getScope() == SymbolScope.BUILTIN) {
            return symbol;
        }
        
        // 其他情況定義為自由變量
        return defineFree(symbol);
    }
    
    return symbol;
}
```

**解析邏輯**：
```
查找符號 "a":
  1. 在當前作用域查找
     找到了？ → 返回
     
  2. 沒找到，有外層作用域？
     遞歸查找外層
     
  3. 在外層找到了
     是 GLOBAL 或 BUILTIN？ → 直接返回
     是 LOCAL 或 FREE？ → 定義為當前作用域的 FREE 變量
```

**為什麼 GLOBAL 不需要作為自由變量？**

因為全局變量在任何地方都可以直接訪問，不需要通過閉包攜帶。

**示例**：
```monkey
let global = 10;

fn(a) {          // a: LOCAL
    fn(b) {      // b: LOCAL
        global + a + b
        // global: GLOBAL (直接訪問)
        // a: FREE (來自外層)
        // b: LOCAL (當前層)
    }
}
```

---

## 編譯流程詳解

### 完整示例：編譯嵌套閉包

**輸入 Monkey 代碼**：
```monkey
fn(a) {
    fn(b) {
        a + b
    }
}
```

### 階段 1：編譯外層函數
```
進入作用域 (外層)
  符號表: SymbolTable{outer=global}
  
  定義參數:
    a → Symbol{name="a", scope=LOCAL, index=0}
    
  開始編譯函數體...
```

### 階段 2：編譯內層函數
```
進入作用域 (內層)
  符號表: SymbolTable{outer=外層}
  
  定義參數:
    b → Symbol{name="b", scope=LOCAL, index=0}
    
  編譯 a + b:
    
    解析 'a':
      1. 在當前作用域查找 → 未找到
      2. 在外層作用域查找 → 找到 Symbol{name="a", scope=LOCAL, index=0}
      3. 不是 GLOBAL 或 BUILTIN
      4. 調用 defineFree(Symbol{a, LOCAL, 0})
      5. 返回 Symbol{name="a", scope=FREE, index=0}
      6. 發射: OpGetFree 0
    
    解析 'b':
      1. 在當前作用域查找 → 找到 Symbol{name="b", scope=LOCAL, index=0}
      2. 發射: OpGetLocal 0
    
    發射: OpAdd
  
  發射: OpReturnValue
  
  獲取自由變量:
    freeSymbols = [Symbol{name="a", scope=LOCAL, index=0}]
  
  離開作用域
  返回指令序列
```

**內層函數的編譯結果**：
```
常量池索引 0:
  OpGetFree 0      // a (自由變量)
  OpGetLocal 0     // b (局部參數)
  OpAdd
  OpReturnValue
```

### 階段 3：在外層函數中處理內層函數
```
回到外層作用域
  符號表: SymbolTable{outer=global}
  
  獲取到內層函數的自由變量:
    freeSymbols = [Symbol{name="a", scope=LOCAL, index=0}]
  
  載入自由變量到堆疊:
    for each symbol in freeSymbols:
      Symbol{name="a", scope=LOCAL, index=0}
      在當前作用域，a 是 LOCAL
      發射: OpGetLocal 0
  
  創建編譯後的函數:
    CompiledFunctionObject{instructions=..., numLocals=1, numParams=1}
  
  添加到常量池:
    常量池索引 0
  
  發射閉包創建指令:
    OpClosure 0 1  // 函數索引=0, 自由變量數量=1
  
  發射: OpReturnValue
  
  離開作用域
```

**外層函數的編譯結果**：
```
常量池索引 1:
  OpGetLocal 0     // a (載入以供內層函數使用)
  OpClosure 0 1    // 創建內層閉包，1 個自由變量
  OpReturnValue
```

### 階段 4：主程序
```
全局作用域
  
  創建外層閉包:
    OpClosure 1 0  // 函數索引=1, 自由變量數量=0
  
  OpPop
```

### 完整編譯結果
```
常量池:
  [0]: CompiledFunction {
         // 內層函數
         OpGetFree 0      // a
         OpGetLocal 0     // b
         OpAdd
         OpReturnValue
       }
  
  [1]: CompiledFunction {
         // 外層函數
         OpGetLocal 0     // a (載入以傳遞給內層)
         OpClosure 0 1    // 創建閉包
         OpReturnValue
       }

主程序指令:
  0000 OpClosure 1 0     // 創建外層閉包
  0004 OpPop
```

---

## VM 執行流程詳解

### 完整示例：執行閉包

**執行代碼**：
```monkey
let newAdder = fn(a) {
    fn(b) { a + b };
};
let addTwo = newAdder(2);
addTwo(3);
```

### 階段 1：創建 newAdder 閉包
```
指令: OpClosure 1 0

執行:
  1. constIndex = 1
  2. numFree = 0
  3. function = constants[1]  // CompiledFunctionObject
  4. free = []  // 空數組，沒有自由變量
  5. closure = ClosureObject{fn=function, free=[]}
  6. push(closure)

堆疊: [ClosureObject{newAdder}]
```
```
指令: OpSetGlobal 0

執行:
  globals[0] = pop()  // ClosureObject{newAdder}

堆疊: []
全局: globals[0] = ClosureObject{newAdder}
```

### 階段 2：調用 newAdder(2)
```
指令: OpGetGlobal 0
堆疊: [ClosureObject{newAdder}]

指令: OpConstant 0  // 2
堆疊: [ClosureObject{newAdder}, 2]
```
```
指令: OpCall 1

執行:
  callee = stack[sp - 1 - 1] = ClosureObject{newAdder}
  numArgs = 1
  
  調用 callClosure:
    1. 檢查參數數量: 1 == 1 ✓
    2. basePointer = sp - numArgs = 2 - 1 = 1
    3. 創建調用幀: Frame{closure=newAdder, basePointer=1}
    4. pushFrame(frame)
    5. sp = basePointer + numLocals = 1 + 1 = 2
    
  堆疊佈局:
    [ClosureObject{newAdder}, 2, ...]
                              ↑
                         basePointer

  執行 newAdder 函數體:
```

#### newAdder 函數內部執行
```
指令: OpGetLocal 0

執行:
  localIndex = 0
  frame = currentFrame()  // Frame{basePointer=1}
  value = stack[1 + 0] = stack[1] = 2
  push(2)

堆疊: [ClosureObject{newAdder}, 2, 2]
```
```
指令: OpClosure 0 1

執行:
  constIndex = 0
  numFree = 1
  function = constants[0]  // 內層函數
  
  收集自由變量:
    free = [stack[sp - 1]]
         = [stack[2]] 
         = [2]  // a 的值
    sp = sp - 1 = 2
  
  創建閉包:
    closure = ClosureObject{
      fn = 內層函數,
      free = [2]  // ⭐ 捕獲了 a 的值
    }
  
  push(closure)

堆疊: [ClosureObject{newAdder}, 2, ClosureObject{inner, free=[2]}]
```
```
指令: OpReturnValue

執行:
  returnValue = pop()  // ClosureObject{inner, free=[2]}
  frame = popFrame()   // 返回到主程序
  sp = frame.basePointer - 1 = 1 - 1 = 0
  push(returnValue)

堆疊: [ClosureObject{inner, free=[2]}]
```
```
指令: OpSetGlobal 1

執行:
  globals[1] = pop()  // ClosureObject{inner, free=[2]}

堆疊: []
全局: globals[1] = ClosureObject{inner, free=[2]}  // addTwo
```

### 階段 3：調用 addTwo(3)
```
指令: OpGetGlobal 1
堆疊: [ClosureObject{inner, free=[2]}]

指令: OpConstant 1  // 3
堆疊: [ClosureObject{inner, free=[2]}, 3]
```
```
指令: OpCall 1

執行:
  callee = ClosureObject{inner, free=[2]}
  numArgs = 1
  
  調用 callClosure:
    basePointer = 1
    創建調用幀: Frame{closure=inner, basePointer=1}
    sp = 1 + 1 = 2
    
  堆疊佈局:
    [ClosureObject{inner}, 3, ...]
                           ↑
                      basePointer
  
  執行內層函數:
```

#### 內層函數執行
```
指令: OpGetFree 0

執行:
  freeIndex = 0
  closure = currentFrame().getClosure()  // ClosureObject{inner, free=[2]}
  value = closure.getFree()[0] = 2  // ⭐ 獲取捕獲的 a
  push(2)

堆疊: [ClosureObject{inner}, 3, 2]
```
```
指令: OpGetLocal 0

執行:
  localIndex = 0
  value = stack[basePointer + 0] = stack[1] = 3  // 參數 b
  push(3)

堆疊: [ClosureObject{inner}, 3, 2, 3]
```
```
指令: OpAdd

執行:
  right = pop() = 3
  left = pop() = 2
  result = 2 + 3 = 5
  push(5)

堆疊: [ClosureObject{inner}, 3, 5]
```
```
指令: OpReturnValue

執行:
  returnValue = pop() = 5
  frame = popFrame()
  sp = frame.basePointer - 1 = 1 - 1 = 0
  push(5)

堆疊: [5]
```

**最終結果**：`5`

---

## 關鍵實現細節

### 1. 自由變量的識別（編譯時）

符號表通過 `resolve` 方法自動識別自由變量：
```java
public Symbol resolve(String name) {
    // 1. 在當前作用域查找
    Symbol symbol = store.get(name);
    if (symbol != null) {
        return symbol;  // 找到了，是局部變量
    }
    
    // 2. 在外層作用域查找
    if (outer != null) {
        symbol = outer.resolve(name);
        if (symbol == null) {
            return null;  // 完全找不到
        }
        
        // 3. 判斷是否需要作為自由變量
        if (symbol.getScope() == SymbolScope.GLOBAL || 
            symbol.getScope() == SymbolScope.BUILTIN) {
            return symbol;  // 全局/內建，直接返回
        }
        
        // 4. 定義為自由變量
        return defineFree(symbol);
    }
    
    return null;
}
```

**關鍵決策**：
- ✅ LOCAL → FREE（需要捕獲）
- ✅ FREE → FREE（繼續傳遞）
- ❌ GLOBAL → GLOBAL（無需捕獲，直接訪問）
- ❌ BUILTIN → BUILTIN（無需捕獲，直接訪問）

### 2. 自由變量的傳遞（編譯時）

在離開函數作用域後，載入所有自由變量：
```java
// 獲取自由變量列表
List<Symbol> freeSymbols = symbolTable.getFreeSymbols();

// 獲取局部變量數量
int numLocals = symbolTable.getNumDefinitions();

// 離開作用域
Instructions instructions = leaveScope();

// ⭐ 關鍵：在外層作用域載入自由變量
for (Symbol s : freeSymbols) {
    loadSymbol(s);  // 根據 s 的作用域發射相應指令
}

// 創建函數
CompiledFunctionObject compiledFn = new CompiledFunctionObject(
    instructions, numLocals, numParams
);

// 發射 OpClosure
emit(Opcode.OP_CLOSURE, fnIndex, freeSymbols.size());
```

**為什麼在離開作用域後載入？**

因為此時我們回到了外層作用域，自由變量在這裡可能是：
- LOCAL 變量（使用 OpGetLocal）
- FREE 變量（使用 OpGetFree）
- GLOBAL 變量（使用 OpGetGlobal）

**示例**：
```monkey
fn(a) {          // 外層
    fn(b) {      // 中間層
        fn(c) {  // 內層
            a + b + c
        }
    }
}
```

內層函數的自由變量：`a`, `b`

離開內層作用域後，在中間層：
```
OpGetFree 0    // a (在中間層是自由變量)
OpGetLocal 0   // b (在中間層是局部變量)
OpClosure 0 2  // 創建內層閉包
```

### 3. 閉包的創建（運行時）

VM 中的 `pushClosure` 方法：
```java
private void pushClosure(int constIndex, int numFree) throws VMException {
    // 1. 獲取編譯後的函數
    MonkeyObject constant = constants.get(constIndex);
    if (!(constant instanceof CompiledFunctionObject)) {
        throw new VMException("not a function: " + constant);
    }
    CompiledFunctionObject function = (CompiledFunctionObject) constant;
    
    // 2. 從堆疊收集自由變量
    MonkeyObject[] free = new MonkeyObject[numFree];
    for (int i = 0; i < numFree; i++) {
        free[i] = stack[sp - numFree + i];
    }
    sp = sp - numFree;  // 調整堆疊指針
    
    // 3. 創建閉包
    ClosureObject closure = new ClosureObject(function, free);
    
    // 4. 推入堆疊
    push(closure);
}
```

**堆疊變化**：
```
執行前:
  [..., free0, free1, free2]
                          ↑
                          sp

收集自由變量:
  free = [free0, free1, free2]

執行後:
  [..., ClosureObject{fn, free=[free0, free1, free2]}]
                                                      ↑
                                                      sp
```

### 4. 自由變量的訪問（運行時）

VM 中處理 `OP_GET_FREE`：
```java
case OP_GET_FREE:
    int freeIndex = ins.get(ip + 1) & 0xFF;
    currentFrame().ip += 1;

    // 從當前閉包獲取自由變量
    ClosureObject currentClosure = currentFrame().getClosure();
    push(currentClosure.getFree()[freeIndex]);
    break;
```

**關鍵點**：
- 從當前調用幀獲取閉包
- 從閉包的 free 數組中取值
- 推入堆疊

### 5. 遞歸函數的支持

**關鍵修改**：在 `compileLetStatement` 中先定義符號
```java
private void compileLetStatement(LetStatement letStmt) throws CompilerException {
    // ⭐ 先定義符號（重要！）
    Symbol symbol = symbolTable.define(letStmt.getName().getValue());
    
    // 然後編譯值
    compile(letStmt.getValue());
    
    // 最後發射賦值指令
    if (symbol.getScope() == SymbolScope.GLOBAL) {
        emit(Opcode.OP_SET_GLOBAL, symbol.getIndex());
    } else {
        emit(Opcode.OP_SET_LOCAL, symbol.getIndex());
    }
}
```

**為什麼這樣做？**
```monkey
let fib = fn(n) {
    if (n < 2) {
        n
    } else {
        fib(n - 1) + fib(n - 2)  // ⭐ 引用自己
    }
};
```

執行順序：
1. `define("fib")` - 符號表中有 `fib`
2. 編譯 `fn(n) { ... }`
3. 在函數體中遇到 `fib(n-1)`
4. `resolve("fib")` - ✅ 找到了！
5. 發射 `OpGetGlobal 0`
6. 編譯完成後，`OpSetGlobal 0` 將閉包賦值給 `fib`

如果順序錯誤（先編譯值再定義符號）：
1. 編譯 `fn(n) { ... }`
2. 在函數體中遇到 `fib(n-1)`
3. `resolve("fib")` - ❌ 未定義！
4. 編譯錯誤

---

## 嵌套閉包

### 三層嵌套示例
```monkey
fn(a) {
    fn(b) {
        fn(c) {
            a + b + c
        }
    }
}
```

### 編譯結果

**最內層函數**（常量池索引 0）：
```
OpGetFree 0     // a (從最外層捕獲)
OpGetFree 1     // b (從中間層捕獲)
OpAdd
OpGetLocal 0    // c (當前層參數)
OpAdd
OpReturnValue
```

**中間層函數**（常量池索引 1）：
```
OpGetFree 0     // a (從外層捕獲，標記為自由變量)
OpGetLocal 0    // b (當前層參數，但需傳給內層)
OpClosure 0 2   // 創建最內層閉包，2 個自由變量
OpReturnValue
```

**最外層函數**（常量池索引 2）：
```
OpGetLocal 0    // a (當前層參數，但需傳給內層)
OpClosure 1 1   // 創建中間層閉包，1 個自由變量
OpReturnValue
```

**主程序**：
```
OpClosure 2 0   // 創建最外層閉包，0 個自由變量
OpPop
```

### 關鍵洞察

對於中間層函數：
- `a` 是**自由變量**（使用 OpGetFree）
- `b` 是**局部變量**（使用 OpGetLocal）
- 但**兩者都需要傳遞給內層函數**

這展示了**自由變量的相對性**：
- 在中間層看來，`a` 是自由變量
- 在內層看來，`a` 和 `b` 都是自由變量
- 但在最外層看來，`a` 只是普通的局部參數

---

## 測試

### 運行測試
```bash
# 運行所有測試
mvn test

# 運行符號表測試
mvn test -Dtest=SymbolTableTest

# 運行編譯器測試
mvn test -Dtest=CompilerTest

# 運行 VM 測試
mvn test -Dtest=VMTest

# 運行特定測試
mvn test -Dtest=SymbolTableTest#testResolveFree
mvn test -Dtest=CompilerTest#testClosures
mvn test -Dtest=VMTest#testClosures
mvn test -Dtest=VMTest#testRecursiveFunctions
```

### 測試覆蓋

#### 符號表測試（SymbolTableTest）

✅ **testResolveFree**
- 自由變量的正確識別
- 嵌套作用域中的解析
- `freeSymbols` 列表的正確性

✅ **testResolveUnresolvableFree**
- 無法解析的變量檢測
- 不會將不存在的變量標記為自由變量

#### 編譯器測試（CompilerTest）

✅ **testClosures**
- 簡單閉包編譯
- 嵌套閉包編譯（三層）
- 全局變量、局部變量和自由變量混合
- OpClosure 指令的正確生成
- OpGetFree 指令的正確生成

✅ **testFunctions** (更新)
- 所有函數都使用 OpClosure

✅ **testFunctionCalls** (更新)
- 函數調用使用閉包

#### VM 測試（VMTest）

✅ **testClosures**
- 簡單閉包執行
- 多參數閉包
- 嵌套閉包執行
- 深度嵌套閉包
- 全局變量與閉包混合
- 多個獨立閉包

✅ **testRecursiveFunctions**
- 簡單遞歸（countdown）
- 斐波那契數列
- 嵌套函數中的遞歸

✅ **testClosuresWithBuiltins**
- 閉包與內建函數結合使用
- 高階函數（map, reduce）

---

## 完整示例

### 示例 1：簡單閉包
```monkey
let newAdder = fn(a) {
    fn(b) { a + b };
};
let addTwo = newAdder(2);
addTwo(3);  // => 5
```

**執行流程**：
1. 創建 `newAdder` 閉包
2. 調用 `newAdder(2)`，返回內層閉包（`free=[2]`）
3. 調用內層閉包 `(3)`，訪問 `free[0]=2`，計算 `2+3=5`

### 示例 2：計數器
```monkey
let newCounter = fn() {
    let count = 0;
    fn() {
        let count = count + 1;
        count
    };
};

let counter = newCounter();
counter();  // => 1
counter();  // => 2
```

**注意**：每次調用都會創建新的 `count` 局部變量，不會真正累加。要實現真正的計數器需要可變狀態。

### 示例 3：柯里化
```monkey
let add = fn(a) {
    fn(b) {
        fn(c) {
            a + b + c
        };
    };
};

let add2 = add(2);
let add2And3 = add2(3);
add2And3(4);  // => 9
```

**執行流程**：
1. `add(2)` → 返回閉包（`free=[2]`）
2. `add2(3)` → 返回閉包（`free=[2, 3]`）
3. `add2And3(4)` → 計算 `2+3+4=9`

### 示例 4：高階函數 - Map
```monkey
let map = fn(arr, f) {
    let iter = fn(arr, accumulated) {
        if (len(arr) == 0) {
            accumulated
        } else {
            iter(rest(arr), push(accumulated, f(first(arr))));
        }
    };
    iter(arr, []);
};

let double = fn(x) { x * 2 };
map([1, 2, 3, 4], double);  // => [2, 4, 6, 8]
```

**閉包的使用**：
- `iter` 捕獲 `f`（自由變量）
- `iter` 遞歸調用自己（全局變量）
- `f` 在每次迭代中被調用

### 示例 5：斐波那契數列
```monkey
let fibonacci = fn(n) {
    if (n < 2) {
        n
    } else {
        fibonacci(n - 1) + fibonacci(n - 2)
    }
};

fibonacci(10);  // => 55
```

**遞歸機制**：
- `fibonacci` 定義為全局變量
- 函數體內引用全局的 `fibonacci`
- 每次調用創建新的調用幀

### 示例 6：閉包與全局變量
```monkey
let global = 10;

let makeAdder = fn(a) {
    fn(b) { global + a + b };
};

let adder = makeAdder(5);
adder(3);  // => 18
```

**變量訪問**：
- `global`: OpGetGlobal（全局訪問）
- `a`: OpGetFree（自由變量）
- `b`: OpGetLocal（局部參數）

---

## 性能考量

### 1. 內存使用

**閉包開銷**：
- 每個閉包對象：16 bytes (對象頭) + 8 bytes (fn指針) + 8 bytes (free數組指針)
- 自由變量數組：8 bytes × 自由變量數量

**示例**：
```monkey
fn(a, b, c) {
    fn() { a + b + c }  // 3 個自由變量
}
```
- 內層閉包：32 bytes + 24 bytes (3個引用) = 56 bytes

### 2. 執行效率

**操作時間複雜度**：
- `OpGetFree`: O(1) - 數組索引訪問
- `OpClosure`: O(n) - n 為自由變量數量
- 閉包調用：與普通函數相同

**vs 普通函數**：
- 創建：閉包需要複製自由變量（+O(n)）
- 調用：開銷相同
- 變量訪問：自由變量需要額外的數組訪問

### 3. 優化機會（未實現）

**可能的優化**：

1. **非閉包函數檢測**
```monkey
   fn(a, b) { a + b }  // 無自由變量，不需要閉包
```
可以避免閉包包裝

2. **共享不可變自由變量**
```monkey
   let x = 10;
   fn() { x }  // x 不可變，可以共享
```

3. **內聯簡單閉包**
```monkey
   let add = fn(a, b) { a + b };
   add(1, 2);  // 可以內聯
```

4. **逃逸分析**
    - 檢測閉包是否逃逸作用域
    - 未逃逸的閉包可以在堆疊上分配

---

## 常見問題

### Q1: 為什麼將每個函數都視為閉包？

**A**: 統一設計，簡化實現

**優點**：
- 統一的調用約定
- 減少特殊情況處理
- 編譯器和 VM 邏輯更簡單
- 後續優化更容易

**代價**：
- 輕微的內存和性能開銷
- 可以通過優化消除

### Q2: 自由變量是如何傳遞的？

**A**: 通過堆疊

**步驟**：
1. 編譯時：離開函數作用域後，將自由變量載入堆疊
2. 運行時：OpClosure 從堆疊收集自由變量
3. 存儲在閉包的 free 數組中
4. OpGetFree 從 free 數組訪問

**示例**：
```
編譯: OpGetLocal 0; OpClosure 0 1
運行: stack[2] → free[0] → closure
```

### Q3: 為什麼需要 FREE 作用域？

**A**: 區分變量的訪問方式

**不同作用域的訪問**：
- GLOBAL → OpGetGlobal（全局存儲）
- LOCAL → OpGetLocal（當前堆疊幀）
- FREE → OpGetFree（閉包的 free 數組）
- BUILTIN → OpGetBuiltin（內建函數表）

每種作用域需要不同的訪問機制。

### Q4: 閉包如何支持遞歸？

**A**: 通過全局或外層作用域的符號

**全局遞歸**：
```monkey
let fib = fn(n) {
    if (n < 2) { n }
    else { fib(n-1) + fib(n-2) }  // 引用全局的 fib
};
```

**局部遞歸**：
```monkey
let outer = fn() {
    let inner = fn(n) {
        if (n == 0) { 0 }
        else { inner(n-1) }  // 引用外層的 inner
    };
    inner(5)
};
```

**關鍵**：先定義符號，再編譯函數體

### Q5: 多個閉包可以共享自由變量嗎？

**A**: 不可以，每個閉包都有自己的副本

**當前實現**：
```monkey
let x = 10;
let f1 = fn() { x };
let f2 = fn() { x };
// f1 和 f2 各有 x 的獨立副本
```

**如果需要共享狀態**：
- 使用全局變量
- 或使用引用類型（如數組、哈希）

### Q6: 閉包的 free 數組是如何排序的？

**A**: 按照符號表中的添加順序

**示例**：
```monkey
fn(a, b) {
    fn() {
        b + a  // 順序: b, a
    }
}
```

符號表解析順序：
1. 解析 `b` → 添加到 freeSymbols[0]
2. 解析 `a` → 添加到 freeSymbols[1]

載入順序：
```
OpGetLocal 1    // b → stack[top]
OpGetLocal 0    // a → stack[top+1]
OpClosure 0 2   // free = [b, a]
```

訪問：
```
OpGetFree 0     // freeSymbols[0] = b
OpGetFree 1     // freeSymbols[1] = a
```

### Q7: 為什麼全局變量不作為自由變量？

**A**: 全局變量在任何地方都可訪問

**原因**：
- 全局變量有全局存儲（globals 數組）
- 不需要通過閉包攜帶
- 直接使用 OpGetGlobal 訪問即可

**效率比較**：
```
全局變量:
  OpGetGlobal 0     // 1 條指令

如果作為自由變量:
  OpGetFree 0       // 1 條指令
  + 創建時複製      // 額外開銷
```

### Q8: 閉包可以修改自由變量嗎？

**A**: 不可以直接修改

**當前實現**：
- 自由變量是值的副本
- 修改不會影響原始值

**示例**：
```monkey
let x = 10;
let f = fn() {
    let x = x + 1;  // 創建新的局部 x，不修改外層 x
    x
};
f();  // => 11
x;    // => 10 (未改變)
```

**如果需要可變狀態**：
- 使用數組或哈希
- 使用全局變量

---

## 與原書的差異

### 語言特性

✅ **強類型數組**
- Java: `MonkeyObject[]`
- Go: `[]object.Object`

✅ **顯式類型轉換**
- Java 需要強制類型轉換
- Go 使用類型斷言

✅ **面向對象設計**
- Java 使用類和接口
- Go 使用結構體和方法

### 設計模式

✅ **不可變對象**
- 閉包的 `fn` 和 `free` 都是 `final`
- 保證線程安全

✅ **空安全**
- 使用 null 檢查
- 避免 NullPointerException

✅ **異常處理**
- 使用異常而非錯誤返回值
- 更符合 Java 慣例

### 命名慣例

✅ **駝峰命名**
- Java: `getFreeSymbols()`
- Go: `FreeSymbols`

✅ **類命名**
- Java: `ClosureObject`
- Go: `object.Closure`

✅ **包結構**
- Java: `com.monkey.object`
- Go: `monkey/object`

---

## 附錄A：完整的操作碼列表（截至第九章）

| 操作碼 | 值 | 操作數 | 描述 | 章節 |
|--------|-----|--------|------|------|
| OP_CONSTANT | 0 | 2 bytes | 載入常量 | Ch2 |
| OP_ADD | 1 | - | 加法 | Ch2 |
| OP_POP | 5 | - | 彈出堆疊頂 | Ch2 |
| OP_SUB | 2 | - | 減法 | Ch3 |
| OP_MUL | 3 | - | 乘法 | Ch3 |
| OP_DIV | 4 | - | 除法 | Ch3 |
| OP_TRUE | 6 | - | 推入 true | Ch3 |
| OP_FALSE | 7 | - | 推入 false | Ch3 |
| OP_EQUAL | 8 | - | 相等比較 | Ch3 |
| OP_NOT_EQUAL | 9 | - | 不等比較 | Ch3 |
| OP_GREATER_THAN | 10 | - | 大於比較 | Ch3 |
| OP_MINUS | 11 | - | 取負 | Ch3 |
| OP_BANG | 12 | - | 邏輯非 | Ch3 |
| OP_JUMP_NOT_TRUTHY | 13 | 2 bytes | 條件跳轉 | Ch4 |
| OP_JUMP | 14 | 2 bytes | 無條件跳轉 | Ch4 |
| OP_NULL | 15 | - | 推入 null | Ch4 |
| OP_GET_GLOBAL | 16 | 2 bytes | 獲取全局變量 | Ch5 |
| OP_SET_GLOBAL | 17 | 2 bytes | 設置全局變量 | Ch5 |
| OP_ARRAY | 18 | 2 bytes | 構建陣列 | Ch6 |
| OP_HASH | 19 | 2 bytes | 構建雜湊表 | Ch6 |
| OP_INDEX | 20 | - | 索引訪問 | Ch6 |
| OP_CALL | 21 | 1 byte | 函數調用 | Ch7 |
| OP_RETURN_VALUE | 22 | - | 返回值 | Ch7 |
| OP_RETURN | 23 | - | 返回（無值） | Ch7 |
| OP_GET_LOCAL | 24 | 1 byte | 獲取局部變量 | Ch7 |
| OP_SET_LOCAL | 25 | 1 byte | 設置局部變量 | Ch7 |
| OP_GET_BUILTIN | 26 | 1 byte | 獲取內建函數 | Ch8 |
| **OP_CLOSURE** | **27** | **2+1 bytes** | **創建閉包** | **Ch9** |
| **OP_GET_FREE** | **28** | **1 byte** | **獲取自由變量** | **Ch9** |

---

## 附錄B：符號作用域對照表

| 作用域 | 用途 | 訪問指令 | 存儲位置 | 章節 |
|--------|------|----------|----------|------|
| GLOBAL | 全局變量 | OpGetGlobal, OpSetGlobal | globals 數組 | Ch5 |
| LOCAL | 局部變量/參數 | OpGetLocal, OpSetLocal | 堆疊幀 | Ch7 |
| BUILTIN | 內建函數 | OpGetBuiltin | Builtins 表 | Ch8 |
| **FREE** | **自由變量** | **OpGetFree** | **閉包 free 數組** | **Ch9** |

---

## 附錄C：調試技巧

### 1. 打印符號表
```java
private void printSymbolTable(SymbolTable table, String prefix) {
    System.out.println(prefix + "Store:");
    for (Map.Entry<String, Symbol> entry : table.store.entrySet()) {
        System.out.println(prefix + "  " + entry.getKey() + " -> " + entry.getValue());
    }
    
    System.out.println(prefix + "Free Symbols:");
    for (Symbol sym : table.getFreeSymbols()) {
        System.out.println(prefix + "  " + sym);
    }
    
    if (table.getOuter() != null) {
        printSymbolTable(table.getOuter(), prefix + "  ");
    }
}
```

### 2. 打印閉包內容
```java
private void printClosure(ClosureObject closure) {
    System.out.println("Closure:");
    System.out.println("  Function: " + closure.getFn());
    System.out.println("  Free variables:");
    for (int i = 0; i < closure.getFree().length; i++) {
        System.out.println("    [" + i + "] = " + closure.getFree()[i].inspect());
    }
}
```

### 3. 追蹤編譯過程
```java
private void compile(Node node) throws CompilerException {
    System.out.println("Compiling: " + node.getClass().getSimpleName());
    System.out.println("Current scope index: " + scopeIndex);
    
    // ... 原有代碼 ...
}
```

### 4. 追蹤 VM 執行
```java
public void run() throws VMException {
    while (currentFrame().ip < currentFrame().instructions().size() - 1) {
        currentFrame().ip++;
        
        System.out.println("IP: " + currentFrame().ip);
        System.out.println("Op: " + op);
        System.out.println("Stack: " + Arrays.toString(Arrays.copyOf(stack, sp)));
        
        // ... 原有代碼 ...
    }
}
```

---

## 章節總結

第九章實現了閉包系統，這是本書最複雜的特性：

### 新增組件

1. **ClosureObject** - 閉包對象（函數 + 自由變量）
2. **FREE 作用域** - 第四種符號作用域
3. **自由變量追蹤** - 符號表擴展

### 新增操作碼

- **OP_CLOSURE** - 創建閉包（2+1 bytes）
- **OP_GET_FREE** - 獲取自由變量（1 byte）

### 關鍵實現

✅ **自動識別自由變量**
- 符號表的 resolve 方法
- 區分 LOCAL、GLOBAL、BUILTIN、FREE

✅ **自由變量傳遞機制**
- 編譯時：離開作用域後載入
- 運行時：OpClosure 收集

✅ **嵌套閉包支持**
- 自由變量的相對性
- FREE → FREE 的傳遞

✅ **遞歸函數支持**
- 先定義符號，再編譯值
- 全局和局部遞歸都支持

✅ **統一的調用約定**
- 所有函數都是閉包
- 簡化編譯器和 VM

### 核心設計決策

1. **將所有函數視為閉包** - 統一設計
2. **通過堆疊傳遞自由變量** - 簡單高效
3. **在離開作用域後載入** - 在正確的作用域
4. **使用數組存儲自由變量** - O(1) 訪問
5. **先定義後編譯** - 支持遞歸

### 實現完整度

- ✅ 簡單閉包
- ✅ 嵌套閉包（任意深度）
- ✅ 遞歸函數（全局和局部）
- ✅ 與內建函數結合
- ✅ 高階函數（map, reduce）

---

## 下一步

完成了閉包實現，Monkey 編譯器已經具備了：
- ✅ 完整的表達式和語句
- ✅ 函數和閉包
- ✅ 複合數據類型（數組、哈希）
- ✅ 內建函數
- ✅ 局部和全局變量
- ✅ 遞歸

這是一個功能完整的編譯器！

可能的擴展方向：
- 性能優化（內聯、逃逸分析）
- 更多內建函數
- 模塊系統
- 類型系統
- 垃圾回收

---


## 許可證

本實現僅供學習使用，遵循原書的教育目的。

---

**恭喜完成第九章！** 🎉

你已經實現了一個支持閉包的完整編譯器，這是編程語言實現中最具挑戰性的特性之一。


# Writing A Compiler In Go — 第十章（Java 完整實現）

## Taking Time：性能測試（Benchmarking）

本目錄包含《**Writing A Compiler In Go**》**第十章：Taking Time** 的**完整 Java 實現**。本章作為全書的總結章節，透過系統化的**性能基準測試**來驗證整個編譯器與虛擬機（VM）的正確性、完整性與效能表現。

---

## 章節概述

第十章聚焦於三個核心主題：

* 🎉 **慶祝完成**：回顧從 Lexer、Parser、Compiler 到 VM 的完整實作旅程
* 📊 **性能基準測試**：量化「編譯器 + VM」相對於直譯器的效能優勢
* 🏆 **成果展示**：以遞歸斐波那契函數驗證所有語言與 VM 功能

---

## 章節目標

* ✅ 驗證編譯器與 VM 的**正確性與穩定性**
* ⏱ 測量整體系統的**實際執行效能**
* 🧠 總結整本書的**設計理念與學習成果**

---

## 性能基準測試（Benchmark）

### 測試目標

使用經典的**遞歸斐波那契函數**作為性能基準：

```monkey
let fibonacci = fn(x) {
    if (x == 0) {
        return 0;
    } else {
        if (x == 1) {
            return 1;
        } else {
            fibonacci(x - 1) + fibonacci(x - 2);
        }
    }
};

fibonacci(N);  // N = 15, 20, 25, ...
```

---

### 為什麼選擇斐波那契？

斐波那契函數同時具備多種「壓力測試」特性：

* 🔁 **大量遞歸調用**（深度與次數）
* 🔀 **多層條件分支**（if / else）
* ➕ **密集算術運算**（整數加減）
* 📚 **堆疊與 Frame 管理**（Call Stack）
* 🔥 **計算密集型任務**，非常適合效能比較

---

## 斐波那契數列參考值

| 輸入            | 結果      | 遞歸調用次數（估計） |
| ------------- | ------- | ---------- |
| fibonacci(10) | 55      | ~177       |
| fibonacci(15) | 610     | ~1,973     |
| fibonacci(20) | 6,765   | ~21,891    |
| fibonacci(25) | 75,025  | ~242,785   |
| fibonacci(30) | 832,040 | ~2,692,537 |

這些數據顯示斐波那契函數會快速放大效能差異，是理想的基準測試案例。

---

## 基準測試工具

### JUnit 5 Benchmark 測試套件

* 📄 **檔案**：`BenchmarkTest.java`
* 🧪 使用 JUnit 5 撰寫，可直接整合至 CI / Maven 流程

#### 功能特色

* ✅ 多個 Fibonacci 測試級別（15 / 20 / 25）
* ⏱ **時間分解統計**：

    * Parsing（解析）
    * Compilation（編譯）
    * Execution（執行）
* 🔁 多次運行取平均值
* 📊 時間比例分析（Compile vs Execute）
* ✔ 結果正確性驗證（防止只測速度不測正確）

---

### 執行方式

```bash
# 執行所有基準測試
mvn test -Dtest=BenchmarkTest

# 執行單一測試
mvn test -Dtest=BenchmarkTest#testFibonacci15
mvn test -Dtest=BenchmarkTest#testFibonacci20
mvn test -Dtest=BenchmarkTest#testFibonacciMultipleRuns
```

---

### 測試列表

| 測試方法                          | 描述     | 輸入                |
| ----------------------------- | ------ | ----------------- |
| `testFibonacci15()`           | 快速效能測試 | fibonacci(15)     |
| `testFibonacci20()`           | 中等強度測試 | fibonacci(20)     |
| `testFibonacci25()`           | 高強度測試  | fibonacci(25)     |
| `testFibonacciMultipleRuns()` | 多次運行平均 | fibonacci(15) × 5 |
| `testCompileVsExecuteRatio()` | 時間分布分析 | fibonacci(15)     |
| `testArrayOperations()`       | 陣列操作效能 | map / reduce      |

---

## 預期性能提升

根據原書（Go 版本）的基準測試結果：

### Fibonacci(35)

| 執行方式                     | 時間   |
| ------------------------ | ---- |
| Tree-walking Interpreter | ~8 秒 |
| Compiler + VM            | ~3 秒 |

➡ **效能提升：約 2.7 倍**

Java 版本在 JVM JIT 優化下，通常可達到相近甚至更佳的表現。

---

## 為什麼編譯器 + VM 更快？

1. **無需重複解析 AST**
   編譯後的位元組碼可重複執行

2. **指令更緊湊**
   Bytecode 比 AST 節點結構更小、更快

3. **分派成本更低**
   `switch-case` Opcode 分派 vs AST 虛擬方法呼叫

4. **符號預先解析**
   變量位置於編譯期已確定

5. **更友善 JVM 優化**
   熱路徑可被 JIT 進一步優化

---

## 章節總結

第十章不再新增語言特性，而是：

* 🔍 **驗證所有功能是否正確整合**
* 📊 **用數據證明設計選擇的價值**
* 🧠 **回顧整個編譯器架構的演進**

這是一次工程與理論的完美收官。

---

## 最後的話

🎊 **恭喜你完成整本《Writing A Compiler In Go》的 Java 實現！** 🎉

你現在已經擁有：

* 🧩 一個**功能完整的編譯器與虛擬機**
* 🧠 對語言設計、編譯流程、VM 架構的深入理解
* 🛠 實際動手打造系統級軟體的寶貴經驗

這不是終點，而是新的起點。

> 繼續學習，繼續構建，繼續創造。

---




