# Khaos

[![Build Status](https://travis-ci.org/ekuinox/Khaos.svg)](https://travis-ci.org/ekuinox/Khaos)

SpigotでDigAllを再現したくて作りました．

## Commands

`/khaos status` ... 機能が有効か確認

`/khaos switch` ... 機能の有効無効を切り替える

`/khaos reload` ... 設定を再読込する

#### Aliases
   - `kh`
   - `ks`

## config.yml

- `allowTools` ... 許可するツールのリスト
```
allowTools:
  WOOD_SPADE:
    - DIRT
    - GRASS
    - SAND
  WOOD_PICKAXE:
    - STONE
```
ツール名およびブロック名は[Material](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html)を参照してください．
- `radius: number` ... 掘削半径．偶数推奨．
- `near: number` ... 手前にいくら掘るかの数値．
- `far: number` ... 奥にいくら掘るかの数値．
- `consume: bool` ... 破壊したブロックの数だけツールの耐久を消費するか．偽の場合でも1は消費します．
- `forceOnSneaking: bool` ... スニーキング中でも機能を強制的に有効にするか．
- `dontDigFloor: bool` ... 自分の居場所より低い場所も掘るか．
- `switchRightClick: bool` ... 右クリックで機能のトグルを行うか． 
- `switchFromPunch: bool` ... 何も持たずブロックに対して右クリックした際に機能のトグルを行うか．

# must watch

[TVアニメ『こみっくがーるず』公式サイト](http://comic-girls.com/)
