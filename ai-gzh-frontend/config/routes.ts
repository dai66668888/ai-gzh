export default [
  {
    path: '/user',
    layout: false,
    routes: [
      { path: '/user/login', component: './User/Login' },
      { path: '/user/register', component: './User/Register' },
    ],
  },
  {
    path: '/material',
    icon: 'file',
    name: '素材管理',
    component: './Material',
  },
  {
    path: '/autoReply',
    icon: 'message',
    name: '自动回复管理',
    component: './AutoReply',
  },
  {
    path: '/menu',
    icon: 'menu',
    name: '菜单管理',
    component: './Menu',
  },
  {
    path: '/account',
    icon: 'appstore',
    name: '公众号管理',
    component: './Account',
  },
  {
    path: '/admin',
    icon: 'crown',
    name: '管理页',
    access: 'canAdmin',
    routes: [
      { path: '/admin', redirect: '/admin/user' },
      { icon: 'table', path: '/admin/user', component: './Admin/User', name: '用户管理' },
    ],
  },
  { path: '/', redirect: '/material' },
  { path: '*', layout: false, component: './404' },
];
