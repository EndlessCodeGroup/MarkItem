## [Unreleased]

## [v1.0-rc1] (2022-08-27)

### Mimic

This version brings integration with Mimic and **makes it required** for MarkItem.
Integration with Mimic allows:
- Get mark item via Mimic API or command. You can use item `markitem:mark` everywhere where Mimic is supported
- Use any items from plugins supporting Mimic as a texture for mark item and in `allowed` and `denied` patterns

### More reliable marks

> **BREAKING CHANGE!** Marks created in v0.5 can't be used anymore.

Since now, we use more reliable mechanism to identify the item as a "mark".
Good news is that lore and name changes will not break marks anymore.
It means you can change mark configurations (name, lore etc.) and you will still be able to use marks created before configuration change.
Bad news is that the new mechanism is not compatible with the mechanism used in version 0.5, so marks created in 0.5 can't be used anymore.

### Config rework

Config was reworked to make it more simple:
- Flag `update` removed
- Options `mark.lore` and `mark.text` turned from string to list of strings
- Option `mark.glow` removed to remove usage of unsafe enchantments API
- Options `allowed` and `denied` take simple patterns instead of regex

### Configurable recipe

The plugin will not add recipes for each material anymore, it will create one-for-all recipe instead.
You can configure title and description of the "Marked Item" recipe that will be shown to player in recipe book.

```yaml
recipe:
  texture: red_dye
  title: "Marked Item"
  description:
    - "You can mark your items to keep it on death"
```

### Fixed

- Fixed the issue when plugin may erase armor kept by other plugin
- Fixed the problem when some items can't be marked

[Unreleased]: https://github.com/EndlessCodeGroup/MarkItem/compare/v1.0-rc1...master
[v1.0-rc1]: https://github.com/EndlessCodeGroup/MarkItem/compare/v0.5...v1.0-rc1
