import { injectGlobalWebcomponentCss } from 'Frontend/generated/jar-resources/theme-util.js';

import '@vaadin/polymer-legacy-adapter/style-modules.js';
import '@vaadin/text-field/theme/lumo/vaadin-text-field.js';
import '@vaadin/password-field/theme/lumo/vaadin-password-field.js';
import '@vaadin/vertical-layout/theme/lumo/vaadin-vertical-layout.js';
import '@vaadin/app-layout/theme/lumo/vaadin-app-layout.js';
import '@vaadin/tooltip/theme/lumo/vaadin-tooltip.js';
import '@vaadin/horizontal-layout/theme/lumo/vaadin-horizontal-layout.js';
import '@vaadin/button/theme/lumo/vaadin-button.js';
import 'Frontend/generated/jar-resources/disableOnClickFunctions.js';
import '@vaadin/common-frontend/ConnectionIndicator.js';
import '@vaadin/vaadin-lumo-styles/sizing.js';
import '@vaadin/vaadin-lumo-styles/spacing.js';
import '@vaadin/vaadin-lumo-styles/style.js';
import '@vaadin/vaadin-lumo-styles/vaadin-iconset.js';
import 'Frontend/generated/jar-resources/ReactRouterOutletElement.tsx';

const loadOnDemand = (key) => {
  const pending = [];
  if (key === 'eaf8e99e02fe4138933551cfeca0554f74d02125d30134660ab11711e81a36b2') {
    pending.push(import('./chunks/chunk-a6cc7937f5ba59e6eafe908291972acd869f607a412114232f2767e399aee3d3.js'));
  }
  if (key === '4e535d9e21cf352cfa4a94c97c4033765706bfb254e9f884aa19f34cda686fba') {
    pending.push(import('./chunks/chunk-a6cc7937f5ba59e6eafe908291972acd869f607a412114232f2767e399aee3d3.js'));
  }
  if (key === '9993181323a2749349cfa1d2d8a7b2d152623f6c4b7fb44c39bf38ed92c3a800') {
    pending.push(import('./chunks/chunk-18eb6bf6b854f0f02b440f39a39442cfb5c2198eccb43d938ad926e9c7b4e4a1.js'));
  }
  if (key === 'a3c79bf05d6656b059d0564cee7ac5decbca874987f5ceb3b50ad60a5cda7cf1') {
    pending.push(import('./chunks/chunk-a6cc7937f5ba59e6eafe908291972acd869f607a412114232f2767e399aee3d3.js'));
  }
  if (key === '449a9e5de36285089dd1ebe279268a6c27333e5ffee44dfd58a729889f0611c5') {
    pending.push(import('./chunks/chunk-a6cc7937f5ba59e6eafe908291972acd869f607a412114232f2767e399aee3d3.js'));
  }
  if (key === '4284809c317dd077d009dd9ce7d411225aa764e6176adee6c01e252d18745a2c') {
    pending.push(import('./chunks/chunk-18eb6bf6b854f0f02b440f39a39442cfb5c2198eccb43d938ad926e9c7b4e4a1.js'));
  }
  if (key === '4c62d2bb4fe0030faecba9b65e4d39273ed0c0c059bca25339d637bc187b04bd') {
    pending.push(import('./chunks/chunk-7e267d016060276d2d8dc0cdbc879494fb4128d6785792a77bc38a59879d97a1.js'));
  }
  if (key === '20b493cc93bee9c50bea6781e655cead69da396ace7a50b1a7751352af50f320') {
    pending.push(import('./chunks/chunk-d65906fd48f681b04bdf1230c35e99c2ca2cc0509ceab9775e781080aea72e5d.js'));
  }
  if (key === 'd95704976f1b06ce4146e64e85d4cfd668d7ae540c6cd3dded8b38cd92729616') {
    pending.push(import('./chunks/chunk-e4c8bdfcf344c3a6b59aff1dc812bee54ec78a473ec1c9a5cfa3b207e7688fa8.js'));
  }
  if (key === '828fb60a4eec95416d46458b5c21996df4581007d4991a3c4ff8706ca709ffa6') {
    pending.push(import('./chunks/chunk-7f62309ba99636022a9442f08ec03402b097463a9ba4a68acdd4520a67799b22.js'));
  }
  return Promise.all(pending);
}

window.Vaadin = window.Vaadin || {};
window.Vaadin.Flow = window.Vaadin.Flow || {};
window.Vaadin.Flow.loadOnDemand = loadOnDemand;
window.Vaadin.Flow.resetFocus = () => {
 let ae=document.activeElement;
 while(ae&&ae.shadowRoot) ae = ae.shadowRoot.activeElement;
 return !ae || ae.blur() || ae.focus() || true;
}