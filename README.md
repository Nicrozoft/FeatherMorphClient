<!-- [Wiki](https://github.com/NiFeather/FeatherMorph/wiki) -->

# (WIP) FeatherMorphClient

[FeatherMorph](https://github.com/NiFeather/FeatherMorph) 的 [客户端模组](https://github.com/NiFeather/FeatherMorphClient) 的 [高版本移植](https://github.com/Nicrozoft/FeatherMorphClient) ，可以提供一些增强功能。

### 功能
- [x] 在客户端显示自身伪装
- [x] 伪装选择界面
- [x] 技能快捷键
- [x] 一键切换自身可见

### 依赖关系
FeatherMorphClient 至少需要下面这些依赖才能运行：
- Fabric, Quilt 1.19.3 或 NeoForge 1.21.5
- [Cloth Config](https://modrinth.com/mod/cloth-config)
- [Fabric API](https://modrinth.com/mod/fabric-api) (NeoForge不需要)

### 构建
```bash
#!/usr/bin/env bash
git clone https://github.com/Nicrozoft/FeatherMorphClient
cd FeatherMorphClient

./gradlew build --no-daemon
```

生成的文件将位于`fabric/build/libs`或`neoforge/build/libs`中，`client-x.x.x.jar`就是构建出来的插件。

### Credits
- [Identity Mod](https://github.com/Draylar/identity): For how I learned to make client-side disguise possible.
- [VeinMiner](https://github.com/2008Choco/VeinMiner): For the reference about how to implement *Client <-> Server* communication.
- [osu-framework](https://github.com/ppy/osu-framework): For the Drawable, Bindable and the Dependency(`@Resolved`) system, although this project has a very poor reimplementation.