import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { getCurrentUser, isLoggedIn } from "../../features/auth/authService";
import Navigation from "./Navigation";
import UserDropdown from "./UserDropdown";
import styles from "./Header.module.css";

export default function Header() {
  const [searchQuery, setSearchQuery] = useState("");
  const [user, setUser] = useState(() => isLoggedIn() ? getCurrentUser() : null);
  const navigate = useNavigate();

  useEffect(() => {
    function syncAuthState() {
      setUser(isLoggedIn() ? getCurrentUser() : null);
    }

    window.addEventListener("auth:login", syncAuthState);
    window.addEventListener("auth:logout", syncAuthState);

    return () => {
      window.removeEventListener("auth:login", syncAuthState);
      window.removeEventListener("auth:logout", syncAuthState);
    };
  }, []);

  return (
    <header className={styles.header}>
      <Navigation />

      <div className={styles.navRight}>
        <div className={styles.searchContainer}>
          <input
            type="text"
            placeholder="Search games..."
            className={styles.searchBox}
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
          <span className={styles.searchIcon}>🔍</span>
        </div>
        
        <div className={styles.iconBtn} onClick={() => navigate("/cart")}>
          🛒
        </div>

        <UserDropdown user={user} />
      </div>
    </header>
  );
}
