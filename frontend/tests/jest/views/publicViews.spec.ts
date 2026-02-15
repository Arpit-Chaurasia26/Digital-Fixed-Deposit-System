import { mount } from '@vue/test-utils';
import { createTestStore } from '../utils/createStore';
import Home from '@/views/Home.vue';
import About from '@/views/About.vue';
import Contact from '@/views/Contact.vue';
import PrivacyPolicy from '@/views/PrivacyPolicy.vue';
import TermsOfService from '@/views/TermsOfService.vue';
import CookiePolicy from '@/views/CookiePolicy.vue';
import NotFound from '@/views/NotFound.vue';
import Profile from '@/views/Profile.vue';
import Login from '@/views/auth/Login.vue';
import Register from '@/views/auth/Register.vue';
import ForgotPassword from '@/views/auth/ForgotPassword.vue';

jest.mock('vue-router', () => ({
  useRouter: () => ({ push: jest.fn(), replace: jest.fn() }),
  useRoute: () => ({ params: {}, query: {}, path: '/', name: 'home', hash: '' }),
}));

jest.mock('@/services/fdService', () => ({
  fdService: {
    getAllSchemes: jest.fn().mockResolvedValue([]),
  },
}));

jest.mock('@/services/authServices', () => ({
  authService: {
    login: jest.fn().mockResolvedValue(undefined),
    register: jest.fn().mockResolvedValue(undefined),
    sendEmailOtp: jest.fn().mockResolvedValue(undefined),
    verifyEmailOtp: jest.fn().mockResolvedValue(undefined),
    sendPasswordOtp: jest.fn().mockResolvedValue(undefined),
    resetPassword: jest.fn().mockResolvedValue(undefined),
  },
}));

const mountView = (component: any, store = createTestStore()) =>
  mount(component, {
    global: {
      plugins: [store as any],
      stubs: {
        RouterLink: true,
        RouterView: true,
        Navbar: true,
        Footer: true,
        UserSidebar: true,
        AdminSidebar: true,
      },
    },
  });

describe('Public and auth views', () => {
  it('renders Home view', () => {
    const wrapper = mountView(Home);
    expect(wrapper.text()).toContain('Fixed Deposit');
  });

  it('renders About view', () => {
    const wrapper = mountView(About);
    expect(wrapper.text()).toMatch(/about us/i);
  });

  it('renders Contact view', () => {
    const wrapper = mountView(Contact);
    expect(wrapper.text()).toContain('Contact');
  });

  it('renders Privacy Policy view', () => {
    const wrapper = mountView(PrivacyPolicy);
    expect(wrapper.text()).toContain('Privacy Policy');
  });

  it('renders Terms of Service view', () => {
    const wrapper = mountView(TermsOfService);
    expect(wrapper.text()).toContain('Terms of Service');
  });

  it('renders Cookie Policy view', () => {
    const wrapper = mountView(CookiePolicy);
    expect(wrapper.text()).toContain('Cookie Policy');
  });

  it('renders NotFound view', () => {
    const wrapper = mountView(NotFound);
    expect(wrapper.text()).toContain('Page Not Found');
  });

  it('renders Profile view', () => {
    const wrapper = mountView(Profile);
    expect(wrapper.text()).toContain('My Profile');
  });

  it('renders Login view', () => {
    const wrapper = mountView(Login);
    expect(wrapper.text()).toContain('Login');
  });

  it('renders Register view', () => {
    const wrapper = mountView(Register);
    expect(wrapper.text()).toContain('Create Account');
  });

  it('renders ForgotPassword view', () => {
    const wrapper = mountView(ForgotPassword);
    expect(wrapper.text()).toContain('Reset Password');
  });
});
