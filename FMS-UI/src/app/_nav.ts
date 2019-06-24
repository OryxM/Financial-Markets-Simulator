interface NavAttributes {
  [propName: string]: any;
}
interface NavWrapper {
  attributes: NavAttributes;
  element: string;
}
interface NavBadge {
  text: string;
  variant: string;
}
interface NavLabel {
  class?: string;
  variant: string;
}

export interface NavData {
  name?: string;
  url?: string;
  //icon?: string;
  title?: boolean;
  children?: NavData[];
  variant?: string;
  attributes?: NavAttributes;
  divider?: boolean;
  class?: string;
  label?: NavLabel;
  wrapper?: NavWrapper;
}

export const navItems: NavData[] = [
  {
    name: 'Portfolio',
    url: '/dashboard',
   // icon: 'icon-speedometer',

  },
  {
    title: true,
    name: 'Actions',
  variant: 'danger',
  },
  {
    name: 'Orders',
    url: '/orders',

    //icon: 'icon-drop'
  },
  {
    name: 'Trade history',
    url: '/trades',
    //icon: 'icon-pencil'
  },
  {
    title: true,
    name: 'Watchlists',
    variant: 'danger',

  },
 {
    name: 'Stock watchlists',
    url: '/assets',
    //icon: 'icon-drop'
  },
 {
    name: 'Create watchlist',
    url: '/assets',
    //icon: 'icon-drop'
  }


];
