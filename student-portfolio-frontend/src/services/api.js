import axios from 'axios';

// Create an Axios instance with a base URL
const api = axios.create({
    baseURL: '/api',
});

// We can add interceptors here later if we need to attach tokens to headers
// api.interceptors.request.use(config => { ... return config; });

export default api;
