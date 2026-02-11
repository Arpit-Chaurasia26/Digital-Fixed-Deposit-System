<template>
 <div class="page">
   <Navbar />
   <div :class="['container', 'page-content', isAdmin ? 'admin-layout' : 'user-layout']">
     <AdminSidebar v-if="isAdmin" />
     <UserSidebar v-if="!isAdmin" />
     <div :class="isAdmin ? 'admin-content' : 'user-content'">
       <div class="page-header card">
         <div>
           <h1>My Profile</h1>
           <p>Update your basic details and keep your account information current.</p>
         </div>
       </div>


       <div class="card panel">
         <div class="profile-grid">
           <div class="profile-card">
             <div class="avatar">{{ initials }}</div>
             <div>
               <h3>{{ form.name || 'Your Name' }}</h3>
               <p class="muted profile-email">{{ form.email || 'you@example.com' }}</p>
               <span class="role-chip">{{ user?.role || 'USER' }}</span>
             </div>
           </div>


           <form class="profile-form" @submit.prevent="saveProfile">
             <div class="form-group">
               <label class="form-label">Full Name</label>
               <input v-model="form.name" type="text" class="form-control" placeholder="Enter your name" />
             </div>
             <div class="form-group">
               <label class="form-label">Email</label>
               <input v-model="form.email" type="email" class="form-control" placeholder="Enter your email" />
             </div>


             <div v-if="error" class="alert alert-error">{{ error }}</div>
             <div v-if="successMessage" class="alert alert-success">{{ successMessage }}</div>


             <button class="btn btn-primary" type="submit" :disabled="loading">
               {{ loading ? 'Saving...' : 'Save Changes' }}
             </button>
           </form>


           <div class="divider"></div>


           <form class="profile-form" @submit.prevent="changePassword">
             <h3>Change Password</h3>
             <div class="form-group">
               <label class="form-label">Current Password</label>
               <input v-model="passwordForm.currentPassword" type="password" class="form-control" placeholder="Enter current password" />
             </div>
             <div class="form-group">
               <label class="form-label">New Password</label>
               <input v-model="passwordForm.newPassword" type="password" class="form-control" placeholder="Enter new password" />
             </div>
             <div class="form-group">
               <label class="form-label">Confirm New Password</label>
               <input v-model="passwordForm.confirmPassword" type="password" class="form-control" placeholder="Confirm new password" />
             </div>


             <div v-if="passwordError" class="alert alert-error">{{ passwordError }}</div>
             <div v-if="passwordSuccess" class="alert alert-success">{{ passwordSuccess }}</div>


             <button class="btn btn-primary" type="submit" :disabled="loading">
               {{ loading ? 'Saving...' : 'Update Password' }}
             </button>
           </form>
         </div>
       </div>
     </div>
   </div>
   <Footer />
 </div>
</template>


<script setup lang="ts">
import { computed, reactive, watch, ref } from 'vue';
import { useStore } from 'vuex';
import Navbar from '@/components/common/Navbar.vue';
import Footer from '@/components/common/Footer.vue';
import AdminSidebar from '@/components/admin/AdminSidebar.vue';
import UserSidebar from '@/components/user/UserSidebar.vue';
import { getErrorMessage } from '@/utils/helpers';


const store = useStore();
const user = computed(() => store.getters['auth/user']);
const isAdmin = computed(() => store.getters['auth/isAdmin']);
const loading = computed(() => store.getters['auth/loading']);
const error = computed(() => store.getters['auth/error']);
const successMessage = ref('');
const passwordError = ref('');
const passwordSuccess = ref('');


const form = reactive({
 name: user.value?.name || '',
 email: user.value?.email || '',
});


const passwordForm = reactive({
 currentPassword: '',
 newPassword: '',
 confirmPassword: '',
});


watch(user, (next) => {
 if (next) {
   form.name = next.name || '';
   form.email = next.email || '';
 }
});


const initials = computed(() => {
 const parts = (form.name || '').trim().split(' ').filter(Boolean);
 if (!parts.length) return 'U';
 return parts.slice(0, 2).map((p: string) => p[0].toUpperCase()).join('');
});


const saveProfile = async () => {
 successMessage.value = '';
 try {
   await store.dispatch('auth/updateProfile', {
     name: form.name.trim(),
     email: form.email.trim(),
   });
   successMessage.value = 'Profile updated successfully.';
 } catch (err) {
   // Error handled by store; ensure meaningful message if not set
   if (!error.value) {
     store.commit('auth/SET_ERROR', getErrorMessage(err, 'Unable to update profile'));
   }
 }
};


const changePassword = async () => {
 passwordError.value = '';
 passwordSuccess.value = '';


 if (!passwordForm.currentPassword || !passwordForm.newPassword || !passwordForm.confirmPassword) {
   passwordError.value = 'Please fill in all password fields.';
   return;
 }


 if (passwordForm.newPassword.length < 8) {
   passwordError.value = 'New password must be at least 8 characters long.';
   return;
 }


 if (passwordForm.newPassword !== passwordForm.confirmPassword) {
   passwordError.value = 'New password and confirm password do not match.';
   return;
 }


 try {
   await store.dispatch('auth/changePassword', {
     currentPassword: passwordForm.currentPassword,
     newPassword: passwordForm.newPassword,
     confirmPassword: passwordForm.confirmPassword,
   });
   passwordSuccess.value = 'Password updated successfully.';
   passwordForm.currentPassword = '';
   passwordForm.newPassword = '';
   passwordForm.confirmPassword = '';
 } catch (err) {
   passwordError.value = getErrorMessage(err, 'Unable to update password');
 }
};
</script>


<style scoped lang="scss">
.page { min-height: 100vh; display: flex; flex-direction: column; }
.page-content { flex: 1; padding: var(--spacing-3xl) var(--spacing-lg); }
.user-content { min-width: 0; }
.page-header { padding: var(--spacing-2xl); background: var(--zeta-gradient-hero); color: white; border-radius: var(--radius-xl); margin-bottom: var(--spacing-2xl); }
.panel { padding: var(--spacing-xl); border-radius: var(--radius-xl); box-shadow: var(--shadow-md); }


.profile-grid {
 display: grid;
 grid-template-columns: minmax(240px, 320px) 1fr;
 gap: var(--spacing-xl);
 align-items: start;
}


.profile-card {
 display: grid;
 gap: var(--spacing-md);
 align-items: center;
 padding: var(--spacing-xl);
 border-radius: var(--radius-xl);
 background: var(--zeta-background);
 border: 1px solid var(--zeta-divider);
}


.avatar {
 width: 72px;
 height: 72px;
 border-radius: 50%;
 background: var(--zeta-gradient-primary);
 color: white;
 display: flex;
 align-items: center;
 justify-content: center;
 font-size: var(--font-size-2xl);
 font-weight: 700;
}


.role-chip {
 display: inline-flex;
 padding: 6px 12px;
 border-radius: 999px;
 background: rgba(79, 70, 229, 0.12);
 color: var(--zeta-primary);
 font-weight: 600;
 font-size: var(--font-size-sm);
}


.profile-form { display: grid; gap: var(--spacing-lg); }
.muted { color: var(--zeta-text-secondary); }
.profile-email { margin-top: -10px; margin-bottom: 10px;}
.divider {
 height: 1px;
 background: var(--zeta-divider);
 margin: var(--spacing-xl) 0;
}


@media (max-width: 1024px) {
 .profile-grid { grid-template-columns: 1fr; }
}
</style>



