import { useState, useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { logoutUser } from "../../features/auth/authService";
import styles from "./Header.module.css";

export default function UserDropdown({ user }) {
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const dropdownRef = useRef(null);
  const navigate = useNavigate();

  useEffect(() => {
    function handleClickOutside(event) {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setIsDropdownOpen(false);
      }
    }

    if (isDropdownOpen) {
      document.addEventListener("mousedown", handleClickOutside);
    }

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [isDropdownOpen]);

  function toggleDropdown() {
    setIsDropdownOpen(!isDropdownOpen);
  }

  function handleNavigate(path) {
    navigate(path);
    setIsDropdownOpen(false);
  }

  function handleLogout() {
    logoutUser();
    setIsDropdownOpen(false);
    window.location.reload();
  }

  const username = user?.profile?.displayName || user?.name || user?.email || "Unknown user";

  return (
    <div className={styles.dropdownContainer} ref={dropdownRef}>
      <div className={styles.iconBtn} onClick={toggleDropdown}>
        👤
      </div>

      {isDropdownOpen && (
        <div className={styles.dropdownMenu}>
          <div className={styles.dropdownHeader}>
            {user ? username : "Not logged in"}
          </div>

          <div
            className={`${styles.dropdownItem} ${styles.dropdownItemBorder}`}
            onClick={() => handleNavigate("/games")}
          >
            🎮 All Games
          </div>

          <div className={styles.dropdownDivider} />

          {user ? (
            <div className={styles.dropdownItem} onClick={handleLogout}>
              🚪 Logout
            </div>
          ) : (
            <>
              <div className={styles.dropdownItem} onClick={() => handleNavigate("/login")}>
                🔑 Login
              </div>
              <div className={styles.dropdownItem} onClick={() => handleNavigate("/register")}>
                ✍️ Sign Up
              </div>
            </>
          )}
        </div>
      )}
    </div>
  );
}
