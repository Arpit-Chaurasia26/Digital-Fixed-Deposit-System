import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import { showAlert } from './composables/useNotifications'
import './assets/styles/main.scss'

const app = createApp(App)

window.alert = (message?: unknown) => {
	showAlert(message)
}

app.use(router)
app.use(store as any)

app.mount('#app')
