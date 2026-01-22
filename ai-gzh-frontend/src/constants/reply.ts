/**
 * 回复规则类型常量
 */
export const REPLY_TYPE = {
  /** 关键词触发 */
  KEYWORD: 0,
  /** 默认回复 */
  DEFAULT: 1,
  /** 关注回复 */
  SUBSCRIBE: 2,
  /** 菜单点击事件 */
  MENU_CLICK: 3,
};

/**
 * 回复内容类型常量
 */
export const CONTENT_TYPE = {
  /** 文本 */
  TEXT: 0,
  /** 图片 */
  IMAGE: 1,
  /** 语音 */
  VOICE: 2,
  /** 视频 */
  VIDEO: 3,
  /** 图文 */
  NEWS: 4,
};

/**
 * 素材类型常量
 */
export const MATERIAL_TYPE = {
  /** 图片 */
  IMAGE: 'image',
  /** 语音 */
  VOICE: 'voice',
  /** 视频 */
  VIDEO: 'video',
  /** 图文 */
  NEWS: 'news',
};

/**
 * 内容类型对应的素材类型映射
 */
export const CONTENT_TO_MATERIAL_TYPE = {
  [CONTENT_TYPE.IMAGE]: MATERIAL_TYPE.IMAGE,
  [CONTENT_TYPE.VOICE]: MATERIAL_TYPE.VOICE,
  [CONTENT_TYPE.VIDEO]: MATERIAL_TYPE.VIDEO,
};

/**
 * 内容类型对应的名称
 */
export const CONTENT_TYPE_NAME = {
  [CONTENT_TYPE.IMAGE]: '图片',
  [CONTENT_TYPE.VOICE]: '语音',
  [CONTENT_TYPE.VIDEO]: '视频',
  [CONTENT_TYPE.TEXT]: '文本',
  [CONTENT_TYPE.NEWS]: '图文',
};

/**
 * 回复类型对应的标签颜色
 */
export const REPLY_TYPE_COLOR = {
  [REPLY_TYPE.KEYWORD]: 'blue',
  [REPLY_TYPE.DEFAULT]: 'green',
  [REPLY_TYPE.SUBSCRIBE]: 'purple',
  [REPLY_TYPE.MENU_CLICK]: 'orange',
};
