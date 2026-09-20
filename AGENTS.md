# AGENTS.md

本文件记录本项目的开发约定与关键改造记录，供后续开发参考。

## 项目概览

- 技术栈：Android / Java、ViewBinding、Material3、RecyclerView + ItemTouchHelper、data-binding 布局。
- 构建：`gradlew.bat :app:assembleDebug`。本机需先设置 `JAVA_HOME`（如 `D:\Android\Android Studio\jbr`）。
- 提交信息风格：`feat(scope): 中文标题` + 中文正文要点；默认**不推送**。

## 约定

1. **不要改动共享基类来做单项功能**：功能相近但归属不同的代码，宁可复制过来，也不要让两者互相继承。
2. 保持项目既有写法与风格，代码方法加简短中文注释。
3. 提交保持未推送状态，除非明确要求推送。
4. 真机行为（手势、布局观感）无法用构建验证，涉及这类改动需说明待验证项。

## 针脚卡片开发约定

### 针脚顺序与界面顺序必须一致

动作的 `pins` 列表顺序即运行期取值顺序。任何拖动排序都必须**回写 `action.getPins()`**，否则界面与取值顺序脱节。

- 一个方位**只能有一个容器**。用两个容器分别装默认针脚和动态针脚，会导致动态针脚在 `pins` 里的位置与界面顺序无法对应。
- 参与排序的针脚在 `pins` 里**可能不连续**（例如 `ChoiceExecuteAction`）。做法是：记下它们原本占用的下标槽位，排序后按新顺序**写回原槽位**，默认针脚不动。
- 成组添加的针脚（如 `MakeMapAction` 一次加键值两条）必须整组一起移动，否则会打乱配对与运行期按下标取值的逻辑。

### 拖动触发方式

- **长按整行拖动**是当前采用的方案（与 `CustomActionCardAdapter` 一致），拖动柄只是给用户看的**视觉提示**，不注册任何事件。
- 不许用长按之外的自定义监听器机制；曾短暂引入 `PinDragAble` 接口 + `OnStartDragListener`，已移除。
- 不可拖动的行要通过 `getMovementFlags()` 返回 `(0, 0)` 从机制上排除，不能只在 `swap` 里判断。
- 拖动柄的显隐由 `PinView.init()` 统一控制：`card instanceof IDynamicPinCard && pin.isDynamic()`，与移除按钮的判断保持一致。

### 针脚放进 RecyclerView 的注意点

- 长按拖动开启后，RecyclerView 会抢走针脚连线的触摸事件。需要 `IDynamicPinCard.suppressLayout()` 在按下时抑制列表滚动——该逻辑已上移到 `PinView.onTouchEvent`，普通针脚也具备。
- itemView 若两个方向都是 `WRAP_CONTENT`，容器内没有余量，`gravity` 与 `layout_gravity` 都不会生效。需要在交叉轴方向撑满才能做顶部/底部、左侧/右侧对齐。

### 默认针脚可移除的前提

把默认针脚改成可移除（`new Pin(..., false, true)`）时，必须同步检查取值逻辑：

- 取值**不能硬编码** `pins.add(firstPin)`——针脚被删除后仍会被读取到陈旧值。应按标志针脚界定范围（`start` 标志 + `addPin`），自然跳过已删除的针脚。
- 若动态针脚被某个固定针脚（如 `resultPin`）**隔成两段**，则默认针脚不适合改成可移除，硬改会破坏原有的分段取值写法。

## 改造记录：动态针脚动作改用独立卡片

提交 `3408a72`（22 个文件，+717 / -161）。

### 背景

原先大量带动态针脚的动作共用 `NormalActionCard`，针脚不可拖动排序。改造分两步：先给列表、字典做了专用卡片（`1d77530`、`f5e9115`），再新建通用卡片承载其余动作。

### 新卡片

- `DynamicPinsActionCard` + `DynamicPinsActionAdapter` + `card_dynamic_pins.xml`。
- 按上、下、左、右**四个方位各一个 RecyclerView**，每个方位只有一个容器，保证界面顺序与 `pins` 顺序一致。
- `getGroupSize()` 读取 `PinAdd.getPins().size()`：一次添加多条针脚的动作，同组并作一行一起拖。
- 拖动排序只作用于动态针脚，默认针脚位置固定。

### 迁入该卡片的动作

`ActionInfo` 中 13 项改为 `DynamicPinsActionCard.class`：开关、选择、顺序、随机、并行、数值相加、数值相乘、文本拼接、或、与、短路与、短路或、位置转手势（数值相减、相除、取模不在其中，仍用 `NormalActionCard`）。

配套的动作改动：

- `SequenceExecuteAction`、`RandomExecuteAction`、`ParallelExecuteAction` 由继承 `ExecuteAction` 改为继承 `Action`，各自声明 `inPin` / `outPin`（`dynamic = true`），并补一个空的 `calculate()`（与 `SwitchAction` 的写法一致）。默认分支针脚改为可移除，因此放开竖直方向拖动。
- `ChoiceExecuteAction` 的 `outPin`、`secondPin` 改为可移除。**`SwitchAction` 有意保持不变。**
- 或、与、短路与、短路或四个布尔动作的默认条件针脚改为可移除；`getDynamicPins()` 改为按 `addPin` 界定范围。这四个动作的空集语义天然成立：或返回 false、与返回 true。
- **数值加/乘、文本拼接放弃改造**：它们的动态针脚被 `resultPin` 隔成两段，默认针脚不适合改成可移除。

### 针脚视图整理

- 拖动能力（`initDragView`、`OnStartDragListener`）曾放在独立接口 `PinDragAble`，**该接口已删除**，相关能力并入 `PinView`，自定义针脚也能直接调用。
- 按住抑制滚动逻辑从 `PinCustomView` 上移到 `PinView`。
- 上下方位的 `pinSlotBox` 改为 `layout_gravity="center_horizontal"`，与标题、针脚控件对齐在同一轴线上；去掉原先的 `marginStart="15dp"`。槽位位置由 `PinView.getSlotPosInLayout()` 实时读取，连线的接点会自动跟随。
- 竖直针脚的移除按钮与拖动柄合并为一行（`MaterialButtonGroup`）。

### 改动顺序上的坑

1. 针脚视图的 `pins` 声明顺序不能变。`reAddPins(...)` 按类**顺序**匹配，顺序一变老任务的针脚会丢失。
2. 先建视图、再按动作针脚顺序重建列表：`addPinView` 里 `getPinView(id)` 需要视图已存在。
3. 移除 `android:visibility="gone"` 时要注意：它可能是该控件的**唯一隐藏机制**，删掉又没有代码接管，控件就会常显。拖动柄就踩过这个坑。
4. 在共享父类上改默认针脚要确认继承范围：`NumberAction` 同时是除法、取模、减法的父类，只应在 `DynamicNumberAction` 上改。

### 待真机验证

构建只能证明编译通过。以下需真机确认：长按拖动排序、针脚连线不被抢手势、默认针脚拖不动、仅动态针脚显示拖动柄、各动作删除针脚后的计算结果、卡片在各方位的布局观感。
