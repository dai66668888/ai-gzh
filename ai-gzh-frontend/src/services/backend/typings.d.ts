declare namespace API {
  type BaseResponseArrayWxMaterialTypeEnum_ = {
    code?: number;
    data?: WxMaterialTypeEnum[];
    message?: string;
  };

  type BaseResponseArrayWxMenuButtonTypeEnum_ = {
    code?: number;
    data?: WxMenuButtonTypeEnum[];
    message?: string;
  };

  type BaseResponseArrayWxReplyContentTypeEnum_ = {
    code?: number;
    data?: WxReplyContentTypeEnum[];
    message?: string;
  };

  type BaseResponseArrayWxReplyMatchTypeEnum_ = {
    code?: number;
    data?: WxReplyMatchTypeEnum[];
    message?: string;
  };

  type BaseResponseArrayWxReplyRuleTypeEnum_ = {
    code?: number;
    data?: WxReplyRuleTypeEnum[];
    message?: string;
  };

  type BaseResponseBoolean_ = {
    code?: number;
    data?: boolean;
    message?: string;
  };

  type BaseResponseLoginUserVO_ = {
    code?: number;
    data?: LoginUserVO;
    message?: string;
  };

  type BaseResponseLong_ = {
    code?: number;
    data?: number;
    message?: string;
  };

  type BaseResponsePageUser_ = {
    code?: number;
    data?: PageUser_;
    message?: string;
  };

  type BaseResponsePageUserVO_ = {
    code?: number;
    data?: PageUserVO_;
    message?: string;
  };

  type BaseResponsePageWxAccountVO_ = {
    code?: number;
    data?: PageWxAccountVO_;
    message?: string;
  };

  type BaseResponsePageWxReplyRuleVO_ = {
    code?: number;
    data?: PageWxReplyRuleVO_;
    message?: string;
  };

  type BaseResponseString_ = {
    code?: number;
    data?: string;
    message?: string;
  };

  type BaseResponseUser_ = {
    code?: number;
    data?: User;
    message?: string;
  };

  type BaseResponseUserVO_ = {
    code?: number;
    data?: UserVO;
    message?: string;
  };

  type BaseResponseWxAccountVO_ = {
    code?: number;
    data?: WxAccountVO;
    message?: string;
  };

  type BaseResponseWxMenuDTO_ = {
    code?: number;
    data?: WxMenuDTO;
    message?: string;
  };

  type BaseResponseWxMpMaterialFileBatchGetResult_ = {
    code?: number;
    data?: WxMpMaterialFileBatchGetResult;
    message?: string;
  };

  type BaseResponseWxMpMaterialVideoInfoResult_ = {
    code?: number;
    data?: WxMpMaterialVideoInfoResult;
    message?: string;
  };

  type BaseResponseWxReplyRuleVO_ = {
    code?: number;
    data?: WxReplyRuleVO;
    message?: string;
  };

  type deleteMaterialUsingPOSTParams = {
    /** appId */
    appId: string;
  };

  type deleteMenuUsingPOSTParams = {
    /** appId */
    appId: string;
  };

  type DeleteRequest = {
    id?: number;
  };

  type downloadImgAndVoiceMaterialUsingGETParams = {
    /** appId */
    appId: string;
    /** fileName */
    fileName?: string;
    /** materialId */
    materialId?: string;
  };

  type getMaterialVideoByMaterialIdUsingGETParams = {
    /** appId */
    appId: string;
    /** materialId */
    materialId: string;
  };

  type getMenuUsingGETParams = {
    /** appId */
    appId: string;
  };

  type getUserByIdUsingGETParams = {
    /** id */
    id?: number;
  };

  type getUserVOByIdUsingGETParams = {
    /** id */
    id?: number;
  };

  type getWxMpAccountVOByIdUsingGETParams = {
    /** id */
    id?: number;
  };

  type getWxMpReplyRuleVOByIdUsingGETParams = {
    /** id */
    id?: number;
  };

  type InputStream = true;

  type InputStreamResource = {
    description?: string;
    file?: string;
    filename?: string;
    inputStream?: InputStream;
    open?: boolean;
    readable?: boolean;
    uri?: string;
    url?: string;
  };

  type listAllMaterialUsingGETParams = {
    /** appId */
    appId: string;
    current?: number;
    materialType?: string;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
  };

  type listWxMpAccountByPageUsingGETParams = {
    /** aesKey */
    aesKey?: string;
    /** 公众号appid */
    appId?: string;
    current?: number;
    /** 公众号名称 */
    name?: string;
    pageSize?: number;
    /** 密钥 */
    secret?: string;
    sortField?: string;
    sortOrder?: string;
    /** token */
    token?: string;
    /** 认证状态 */
    verified?: boolean;
  };

  type listWxMpReplyRuleByPageUsingGETParams = {
    /** 公众号appid */
    appId?: string;
    current?: number;
    /** 菜单栏点击事件key */
    eventKey?: string;
    /** 关键字搜索 */
    matchValue?: string;
    pageSize?: number;
    /** 回复内容 */
    replyContent?: string;
    /** 0 为关键词触发、1 为默认触发、2 为被关注触发、3 为菜单点击事件类型 */
    replyType?: number;
    /** 规则描述 */
    ruleDescription?: string;
    /** 规则名称 */
    ruleName?: string;
    sortField?: string;
    sortOrder?: string;
  };

  type LoginUserVO = {
    createTime?: string;
    id?: number;
    updateTime?: string;
    userAvatar?: string;
    userName?: string;
    userProfile?: string;
    userRole?: string;
  };

  type MaterialDeleteRequest = {
    materialId?: string;
  };

  type ModelAndView = {
    empty?: boolean;
    model?: Record<string, any>;
    modelMap?: Record<string, any>;
    reference?: boolean;
    status?:
      | 'ACCEPTED'
      | 'ALREADY_REPORTED'
      | 'BAD_GATEWAY'
      | 'BAD_REQUEST'
      | 'BANDWIDTH_LIMIT_EXCEEDED'
      | 'CHECKPOINT'
      | 'CONFLICT'
      | 'CONTINUE'
      | 'CREATED'
      | 'DESTINATION_LOCKED'
      | 'EXPECTATION_FAILED'
      | 'FAILED_DEPENDENCY'
      | 'FORBIDDEN'
      | 'FOUND'
      | 'GATEWAY_TIMEOUT'
      | 'GONE'
      | 'HTTP_VERSION_NOT_SUPPORTED'
      | 'IM_USED'
      | 'INSUFFICIENT_SPACE_ON_RESOURCE'
      | 'INSUFFICIENT_STORAGE'
      | 'INTERNAL_SERVER_ERROR'
      | 'I_AM_A_TEAPOT'
      | 'LENGTH_REQUIRED'
      | 'LOCKED'
      | 'LOOP_DETECTED'
      | 'METHOD_FAILURE'
      | 'METHOD_NOT_ALLOWED'
      | 'MOVED_PERMANENTLY'
      | 'MOVED_TEMPORARILY'
      | 'MULTIPLE_CHOICES'
      | 'MULTI_STATUS'
      | 'NETWORK_AUTHENTICATION_REQUIRED'
      | 'NON_AUTHORITATIVE_INFORMATION'
      | 'NOT_ACCEPTABLE'
      | 'NOT_EXTENDED'
      | 'NOT_FOUND'
      | 'NOT_IMPLEMENTED'
      | 'NOT_MODIFIED'
      | 'NO_CONTENT'
      | 'OK'
      | 'PARTIAL_CONTENT'
      | 'PAYLOAD_TOO_LARGE'
      | 'PAYMENT_REQUIRED'
      | 'PERMANENT_REDIRECT'
      | 'PRECONDITION_FAILED'
      | 'PRECONDITION_REQUIRED'
      | 'PROCESSING'
      | 'PROXY_AUTHENTICATION_REQUIRED'
      | 'REQUESTED_RANGE_NOT_SATISFIABLE'
      | 'REQUEST_ENTITY_TOO_LARGE'
      | 'REQUEST_HEADER_FIELDS_TOO_LARGE'
      | 'REQUEST_TIMEOUT'
      | 'REQUEST_URI_TOO_LONG'
      | 'RESET_CONTENT'
      | 'SEE_OTHER'
      | 'SERVICE_UNAVAILABLE'
      | 'SWITCHING_PROTOCOLS'
      | 'TEMPORARY_REDIRECT'
      | 'TOO_EARLY'
      | 'TOO_MANY_REQUESTS'
      | 'UNAUTHORIZED'
      | 'UNAVAILABLE_FOR_LEGAL_REASONS'
      | 'UNPROCESSABLE_ENTITY'
      | 'UNSUPPORTED_MEDIA_TYPE'
      | 'UPGRADE_REQUIRED'
      | 'URI_TOO_LONG'
      | 'USE_PROXY'
      | 'VARIANT_ALSO_NEGOTIATES';
    view?: View;
    viewName?: string;
  };

  type OrderItem = {
    asc?: boolean;
    column?: string;
  };

  type PageUser_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: User[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type PageUserVO_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: UserVO[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type PageWxAccountVO_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: WxAccountVO[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type PageWxReplyRuleVO_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: WxReplyRuleVO[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type updateMenuUsingPOSTParams = {
    /** appId */
    appId: string;
  };

  type uploadMaterialUsingPOSTParams = {
    /** appId */
    appId: string;
    /** materialType */
    materialType?: string;
  };

  type User = {
    createTime?: string;
    id?: number;
    isDelete?: number;
    mpOpenId?: string;
    unionId?: string;
    updateTime?: string;
    userAccount?: string;
    userAvatar?: string;
    userName?: string;
    userPassword?: string;
    userProfile?: string;
    userRole?: string;
  };

  type UserAddRequest = {
    userAccount?: string;
    userAvatar?: string;
    userName?: string;
    userRole?: string;
  };

  type UserLoginRequest = {
    userAccount?: string;
    userPassword?: string;
  };

  type UserQueryRequest = {
    current?: number;
    id?: number;
    mpOpenId?: string;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    unionId?: string;
    userName?: string;
    userProfile?: string;
    userRole?: string;
  };

  type UserRegisterRequest = {
    checkPassword?: string;
    userAccount?: string;
    userPassword?: string;
  };

  type UserUpdateMyRequest = {
    userAvatar?: string;
    userName?: string;
    userProfile?: string;
  };

  type UserUpdateRequest = {
    id?: number;
    userAvatar?: string;
    userName?: string;
    userProfile?: string;
    userRole?: string;
  };

  type UserVO = {
    createTime?: string;
    id?: number;
    userAvatar?: string;
    userName?: string;
    userProfile?: string;
    userRole?: string;
  };

  type View = {
    contentType?: string;
  };

  type WxAccountAddDTO = {
    /** aesKey */
    aesKey: string;
    /** 公众号appid */
    appId: string;
    /** 公众号名称 */
    name: string;
    /** 密钥 */
    secret: string;
    /** token */
    token: string;
  };

  type WxAccountUpdateDTO = {
    /** aesKey */
    aesKey?: string;
    /** appid */
    appId?: string;
    /** id */
    id: number;
    /** 公众号名称 */
    name?: string;
    /** 密钥 */
    secret?: string;
    /** token */
    token?: string;
  };

  type WxAccountVO = {
    aesKey?: string;
    appId?: string;
    createTime?: string;
    id?: number;
    name?: string;
    secret?: string;
    token?: string;
    updateTime?: string;
    user?: UserVO;
    userId?: number;
    verified?: boolean;
  };

  type WxMaterialFileBatchGetNewsItem = {
    mediaId?: string;
    name?: string;
    updateTime?: string;
    url?: string;
  };

  type WxMaterialTypeEnum = {
    label?: string;
    value?: string;
  };

  type WxMenuButtonDTO = {
    appId?: string;
    articleId?: string;
    mediaId?: string;
    /** 菜单KEY值，用于消息接口推送，不超过128字节，click必填 */
    menuKey?: string;
    /** 菜单标题，不超过16个字节，子菜单不超过60个字节 */
    name: string;
    pagePath?: string;
    /** 子菜单 */
    subButtons?: WxMenuButtonDTO[];
    /** 菜单的类型 */
    type?: string;
    /** 网页链接，view、miniprogram 类型必填 */
    url?: string;
  };

  type WxMenuButtonTypeEnum = {
    label?: string;
    value?: string;
  };

  type WxMenuDTO = {
    conditionalMenu?: WxMpConditionalMenu[];
    menu?: WxMpConditionalMenu;
  };

  type WxMenuRule = {
    city?: string;
    clientPlatformType?: string;
    country?: string;
    language?: string;
    province?: string;
    sex?: string;
    tagId?: string;
  };

  type WxMpConditionalMenu = {
    buttons?: WxMenuButtonDTO[];
    menuId?: string;
    rule?: WxMenuRule;
  };

  type WxMpMaterialFileBatchGetResult = {
    itemCount?: number;
    items?: WxMaterialFileBatchGetNewsItem[];
    totalCount?: number;
  };

  type WxMpMaterialVideoInfoResult = {
    description?: string;
    downUrl?: string;
    title?: string;
  };

  type WxReplyContentDTO = {
    /** 图文消息的id，可以调用获取图文消息接口获取 */
    articleId?: string;
    /** 回复内容类型，默认为文字类型 */
    contentType: number;
    /** 素材Id（图片、音频、视频等） */
    mediaId?: string;
    /** 文本消息内容 */
    textContent?: string;
  };

  type WxReplyContentTypeEnum = {
    label?: string;
    value?: number;
  };

  type WxReplyMatchTypeEnum = {
    label?: string;
    value?: number;
  };

  type WxReplyMatchValueDTO = {
    /** 关键字 */
    matchKeyWords?: string;
    /** 匹配类型（全匹配还是半匹配）,默认为模糊匹配 */
    matchType?: number;
  };

  type WxReplyRuleAddDTO = {
    /** 公众号appid */
    appId: string;
    /** 菜单栏点击事件的key */
    eventKey?: string;
    /** 匹配值（关键字的key） */
    matchValue?: WxReplyMatchValueDTO[];
    replyContent: WxReplyContentDTO;
    /** 0 为关键词触发、1 为默认触发、2 为被关注触发、3 为菜单点击事件类型 */
    replyType: number;
    /** 规则描述 */
    ruleDescription?: string;
    /** 规则名称 */
    ruleName: string;
  };

  type WxReplyRuleTypeEnum = {
    label?: string;
    value?: number;
  };

  type WxReplyRuleUpdateDTO = {
    /** appid */
    appId?: string;
    /** 菜单栏点击事件的key */
    eventKey?: string;
    /** id */
    id: number;
    /** 匹配值（关键字或者事件的key） */
    matchValue?: WxReplyMatchValueDTO[];
    replyContent?: WxReplyContentDTO;
    /** 0 为关键词触发、1 为默认触发、2 为被关注触发、3 为菜单点击事件类型 */
    replyType?: number;
    /** 规则描述 */
    ruleDescription?: string;
    /** 规则名称 */
    ruleName?: string;
  };

  type WxReplyRuleVO = {
    appId?: string;
    createTime?: string;
    id?: number;
    /** 匹配值（关键字或者事件的key） */
    matchValue?: WxReplyMatchValueDTO[];
    replyContent?: WxReplyContentDTO;
    replyType?: number;
    ruleDescription?: string;
    ruleName?: string;
    updateTime?: string;
    user?: UserVO;
    userId?: number;
  };
}
