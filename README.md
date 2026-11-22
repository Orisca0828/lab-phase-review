# 控制台扫雷 Minesweeper

回声实验室 2026 年 Java 方向招新阶段性考核 · 选项一：控制台扫雷游戏。

纯 Java 实现、无第三方依赖，在标准控制台（IDEA Run 窗口 / Windows Terminal / CMD）中运行。

## 一、功能清单

### 1. 基础需求

| 需求 | 实现说明 |
| ---- | -------- |
| 网格与地雷模型 | 支持 9×9（10 雷）起，单元格包含「未翻开 / 已翻开 / 插旗」三种状态 |
| 坐标系统 | 行号 1~N、列号 1~N，棋盘上方与左侧都有坐标标识 |
| 首步安全保底 | 首次翻开后才布雷，并且首点周围 3×3 范围内一定没有雷 |
| 交互指令解析 | `o 行 列` / `open 行 列` 翻开，`f 行 列` / `flag 行 列` 插旗，支持直接输入 `行 列` |
| 操作合法性校验 | 已翻开的格子不能重复翻开、不能插旗；插旗的格子翻开时会被拦截并提示 |
| 空白级联展开 | 翻开周围雷数为 0 的格子时，用 **BFS（队列）** 自动展开周围 8 格，直到数字格停止 |
| 胜负结算 | 踩雷后亮出全部地雷并结束；所有安全格翻开即获胜 |
| 棋盘渲染 | 每次操作后重新渲染，行列对齐，显示剩余雷数、已翻开数量与耗时 |

### 2. 加分项（已实现）

- 终端 ANSI 颜色：1 蓝、2 绿、3 红、4 紫、5 及以上青色，地雷红色加粗，旗子红色。
- 游戏计时：从开局到结束统计用时，显示为 `mm:ss`。
- 自定义难度：内置初级 9×9/10 雷、中级 12×12/20 雷、高级 16×16/40 雷，也支持 `行 列 雷数` 自定义。
- 输入健壮性：非数字、空输入、越界坐标、负数、超大棋盘等全部给出友好提示并重新输入，不会抛出未捕获异常。

## 二、系统架构

采用简单的分层结构，职责单一、低耦合：

```text
┌──────────────────────────────┐
│ App（入口 / 菜单流程调度）     │
└───────────────┬──────────────┘
                │ 调用
┌───────────────▼──────────────┐      ┌───────────────────────────┐
│ GameService（核心业务规则）   │◄─────┤ MinePlacer（首点安全布雷） │
└───────┬───────────────┬──────┘      └───────────────────────────┘
        │ 读写数据       │ 返回结果
┌───────▼────────┐  ┌───▼────────────────────┐
│ Board / Cell   │  │ ConsoleView（渲染与提示）│
│ Position 等模型 │  └────────────────────────┘
└────────────────┘              ▲
                                │ 使用
                    ┌───────────┴────────────┐
                    │ ConsoleInput/CommandParser │
                    └────────────────────────┘
```

### 主要类职责

| 类 | 所在包 | 职责 |
| -- | ------ | ---- |
| `App` | `com.liziyan.minesweeper` | 程序入口，处理菜单循环、难度选择，调度 service 与 view |
| `GameConfig` | `model` | 棋盘配置（行、列、雷数），构造时完成范围校验 |
| `Board` | `model` | 棋盘数据：格子矩阵、邻格查询、翻开/插旗/亮雷，不处理输入输出 |
| `Cell` | `model` | 单个格子的状态（地雷、翻开、插旗、周围雷数），修改方法仅包内可见 |
| `Position` | `model` | 不可变坐标对象，提供偏移与相等性判断 |
| `Difficulty` | `model` | 初/中/高三级难度的枚举配置 |
| `GameState` | `model` | 进行中 / 胜利 / 失败三种状态 |
| `GameService` | `service` | 核心规则：首点布雷、翻开、BFS 级联、插旗、胜负判定、计时 |
| `MinePlacer` | `service` | 布雷策略：首次翻开后布雷并保证首点 3×3 安全 |
| `MoveResult` | `service` | 一次操作的结果枚举，携带面向用户的提示文字 |
| `ConsoleView` | `view` | 棋盘渲染、菜单与提示输出，含 ANSI 颜色处理 |
| `ConsoleInput` | `util` | 控制台读行封装，处理 IO 异常与输入流结束 |
| `CommandParser` | `util` | 指令解析（动作 + 坐标），非法输入返回错误提示而不抛异常 |
| `AnsiColor` | `util` | ANSI 颜色常量，支持用 `NO_COLOR` 关闭颜色 |

数据流：`App` 读取一行 → `CommandParser` 解析成 `Command` → `GameService` 执行并返回 `MoveResult` → `ConsoleView` 渲染。

## 三、目录结构

```text
ConsoleMineSweeper/
├── src/
│   └── com/liziyan/minesweeper/
│       ├── App.java
│       ├── model/
│       │   ├── Board.java
│       │   ├── Cell.java
│       │   ├── Difficulty.java
│       │   ├── GameConfig.java
│       │   ├── GameState.java
│       │   └── Position.java
│       ├── service/
│       │   ├── GameService.java
│       │   ├── MinePlacer.java
│       │   └── MoveResult.java
│       ├── util/
│       │   ├── AnsiColor.java
│       │   ├── CommandParser.java
│       │   └── ConsoleInput.java
│       └── view/
│           └── ConsoleView.java
├── .gitignore
└── README.md
```

## 四、编译与运行

### 1. 命令行

在项目根目录执行（JDK 17 及以上，本项目在 JDK 25 下验证）：

```bash
mkdir -p out
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out com.liziyan.minesweeper.App
```

Windows CMD / PowerShell：

```bat
javac -encoding UTF-8 -d out (Get-ChildItem src -Recurse -Filter *.java | ForEach-Object { $_.FullName })
java -cp out com.liziyan.minesweeper.App
```

### 2. IDEA

用 IDEA 打开项目目录，把 `src` 标记为 Sources Root（IDEA 通常会自动识别），
直接运行 `com.liziyan.minesweeper.App` 的 `main` 方法即可。

> 说明：`.idea/`、`*.iml` 属于本地 IDE 配置，已被 `.gitignore` 忽略，不会提交到仓库；
> clone 之后 IDEA 会重新生成本地配置。

### 3. 关闭颜色

部分终端不支持 ANSI 转义序列时，可设置环境变量后再运行：

```bat
set NO_COLOR=1
java -cp out com.liziyan.minesweeper.App
```

## 五、操作说明

| 操作 | 指令 | 示例 |
| ---- | ---- | ---- |
| 翻开格子 | `o 行 列` 或 `open 行 列` 或直接 `行 列` | `o 3 5`、`3 5` |
| 插旗 / 取消插旗 | `f 行 列` 或 `flag 行 列` | `f 1 1` |
| 退出本局 | `q` | `q` |
| 退出游戏 | 主菜单输入 `q` / `quit` | `quit` |

一次典型的运行流程：

```text
请选择 > 1                  ← 选择初级 9×9，10 雷
> o 5 5                      ← 首次翻开，保证不是雷
> f 1 1                      ← 插旗
> o 1 1                      ← 插旗位置翻开会被拦截
[错误] 该格子插着旗，请先取消插旗再翻开
> 20 20                      ← 越界坐标
[错误] 坐标超出范围：行 1~9，列 1~9
```

## 六、关键实现说明

### 1. 首点安全

首次翻开前棋盘上没有任何雷；第一次有效翻开后，`MinePlacer` 排除首点周围 3×3 的格子，
在剩余位置中随机布雷并计算邻雷数。这样首点必定是 0，能直接触发一次级联展开，体验接近扫雷常规规则。

### 2. BFS 级联展开

`GameService.cascadeReveal` 使用 `ArrayDeque` 作为队列：

1. 起点入队；
2. 出队一个格子，若已翻开/插旗/是雷则跳过；
3. 翻开该格并计数；
4. 若该格邻雷数为 0，把未翻开、未插旗、非雷的 8 个邻格入队；
5. 队列为空时结束。

用队列而不是递归，避免大棋盘（例如 50×50）展开时递归过深导致 `StackOverflowError`。

### 3. 封装与职责拆分

- `Cell` 的 setter 是包级可见，只有同包的 `Board` 能修改，外部无法绕过棋盘改状态；
- 界面层 `ConsoleView` 不判断规则，业务层 `GameService` 不打印任何内容；
- 所有输入解析集中在 `CommandParser`，非法输入返回错误对象而不是抛异常。

### 4. 输入健壮性

- 菜单：非数字、格式不符、负数、超大棋盘都会提示并重新选择，`GameConfig` 负责最终校验；
- 对局：坐标非整数、越界、空输入、重复操作都会被拦截并给中文提示；
- 读不到输入（输入流结束）时按退出处理，不产生 `NullPointerException`。

## 七、遇到的困难与解决体会

1. **首点必踩雷的问题**：一开始在构造棋盘时就随机布雷，导致第一次翻开经常直接踩雷。
   解决：把布雷推迟到第一次有效翻开之后，并排除首点周围 3×3；若棋盘小到放不下，退化为只排除首点本身。

2. **级联展开写成递归后栈溢出**：改成递归实现后，在较大棋盘上偶发 `StackOverflowError`。
   解决：改用 BFS 队列实现，逐个出队展开，复杂度 O(格子数)，也不会栈溢出。

3. **插旗与翻开的冲突**：早期版本对插旗格子直接翻开，不符合扫雷习惯。
   解决：在业务层拦截并返回 `FLAGGED` 结果，由界面提示「请先取消插旗」，交互更清晰。

4. **控制台颜色导致对齐错位**：ANSI 转义序列虽然不显示，但会影响字符串长度。
   解决：渲染时按「可见字符数」补齐，例如每个格子固定输出三个可见字符（`  #`、`  1`、`  F`），
   颜色只包裹内容，不影响列对齐。

5. **输入校验分散**：最初在 `main` 里到处 `try-catch`，代码越来越长。
   解决：抽出 `CommandParser` 与 `GameConfig`，把「解析」和「校验」集中处理，
   `main` 只保留菜单流程，符合单一职责。

## 八、Git 提交说明

开发过程按「骨架 → 模型 → 业务 → 界面 → 入口 → 文档」分阶段提交，
提交信息使用 `feat:` / `fix:` / `docs:` / `chore:` 前缀，便于回溯每一步改动的意图。
