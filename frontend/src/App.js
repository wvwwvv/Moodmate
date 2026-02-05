import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import axios from "axios";

import Login from "./pages/Login";
import Chat from "./pages/Chat";
import Collection from "./pages/Collection";
import Calendar from "./pages/Calendar";
import CalendarDetail from "./pages/CalendarDetail";
import Settings from "./pages/Settings";
import AccountSettings from "./pages/AccountSettings";
import TimezoneSettings from "./pages/TimezoneSettings";
import Terms from "./pages/Terms";
import Privacy from "./pages/Privacy";
import TermsAgreement from "./pages/TermsAgreement";

import { AuthProvider } from "./contexts/AuthContext";
import PrivateRoute from "./components/PrivateRoute";

axios.defaults.withCredentials = true;

function AppContent() {
  return (
    <div>
      <Routes>
        {/* 공개 라우트 */}
        <Route path="/" element={<Login />} />
        <Route path="/terms-agreement" element={<TermsAgreement />} />
        <Route path="/terms" element={<Terms />} />
        <Route path="/privacy" element={<Privacy />} />

        {/* 로그인 인증 필요 라우트 */}
        <Route element={<PrivateRoute />}>
          <Route path="/chat" element={<Chat />} />
          <Route path="/collection" element={<Collection />} />
          <Route path="/calendar/:year?/:month?" element={<Calendar />} />
          <Route path="/calendar/detail/:date" element={<CalendarDetail />} />
          <Route path="/settings" element={<Settings />} />
          <Route path="/settings/account" element={<AccountSettings />} />
          <Route path="/settings/timezone" element={<TimezoneSettings />} />
        </Route>
      </Routes>
    </div>
  );
}

function App() {
  return (
    <Router>
      <AuthProvider>
        <AppContent />
      </AuthProvider>
    </Router>
  );
}

export default App;
