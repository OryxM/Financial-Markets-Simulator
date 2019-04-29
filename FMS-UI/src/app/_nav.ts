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
    name: 'Home',
    url: '/dashboard',
   // icon: 'icon-speedometer',
    
  },
  {
    title: true,
    name: 'Portfolio'
  },
  {
    name: 'Orders',
    url: '/orders',
    //icon: 'icon-drop'
  },
  {
    name: 'Trades',
    url: '/theme/typography',
    //icon: 'icon-pencil'
  },
  {
    title: true,
    name: 'Watchlists'
  },
 {
    name: 'Stock',
    url: '/assets', 
    //icon: 'icon-drop'
  },
  {
    name: 'base',
    url: '/base',
   // icon: 'icon-puzzle',
    children: [
      {
        name: 'Cards',
        url: '/base/cards',
      //  icon: 'icon-puzzle'
      },
      {
        name: 'Carousels',
        url: '/base/carousels',
        // icon: 'icon-puzzle'
      },
      {
        name: 'Collapses',
        url: '/base/collapses',
       // icon: 'icon-puzzle'
      },
      {
        name: 'Forms',
        url: '/base/forms',
        // icon: 'icon-puzzle'
      },
      {
        name: 'Pagination',
        url: '/base/paginations',
       // icon: 'icon-puzzle'
      },
      {
        name: 'Popovers',
        url: '/base/popovers',
       // icon: 'icon-puzzle'
      },
      {
        name: 'Progress',
        url: '/base/progress',
       // icon: 'icon-puzzle'
      },
      {
        name: 'Switches',
        url: '/base/switches',
        // icon: 'icon-puzzle'
      },
      {
        name: 'Tables',
        url: '/base/tables',
        // icon: 'icon-puzzle'
      },
      {
        name: 'Tabs',
        url: '/base/tabs',
      //s  icon: 'icon-puzzle'
      },
      {
        name: 'Tooltips',
        url: '/base/tooltips',
       //  icon: 'icon-puzzle'
      }
    ]
  },
  {
    name: 'Buttons',
    url: '/buttons',
   // icon: 'icon-cursor',
    children: [
      {
        name: 'Buttons',
        url: '/buttons/buttons',
      //  icon: 'icon-cursor'
      },
      {
        name: 'Dropdowns',
        url: '/buttons/dropdowns',
      //  icon: 'icon-cursor'
      },
      {
        name: 'Brand Buttons',
        url: '/buttons/brand-buttons',
      //  icon: 'icon-cursor'
      }
    ]
  },
 
  {
    name: 'Icons',
    url: '/icons',
   // icon: 'icon-star',
    children: [
      {
        name: 'CoreUI Icons',
        url: '/icons/coreui-icons',
       // icon: 'icon-star'
       
      },
      {
        name: 'Flags',
        url: '/icons/flags',
       // icon: 'icon-star'
      },
      {
        name: 'Font Awesome',
        url: '/icons/font-awesome',
        //icon: 'icon-star',
        
      },
      {
        name: 'Simple Line Icons',
        url: '/icons/simple-line-icons',
       // icon: 'icon-star'
      }
    ]
  },
  
  {
    name: 'Widgets',
    url: '/widgets',
   // icon: 'icon-calculator',
   
  },
  {
    divider: true
  },
  {
    title: true,
    name: 'Extras',
  },
  

];
