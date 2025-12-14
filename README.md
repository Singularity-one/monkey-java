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

## Writing An Interpreter In Go 第二章