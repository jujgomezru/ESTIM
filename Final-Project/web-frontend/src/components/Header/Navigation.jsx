import { useNavigate, useLocation } from "react-router-dom";
import styles from "./Header.module.css";

export default function Navigation() {
  const navigate = useNavigate();
  const location = useLocation();

  function isActive(path) {
    if (path === "/") {
      return location.pathname === "/" || location.pathname === "/store";
    }
    return location.pathname === path;
  }

  const navLinks = [
    { path: "/", label: "Store" },
    { path: "/library", label: "Library" },
    { path: "/wishlist", label: "💝 Wishlist" },
  ];

  return (
    <div className={styles.navLeft}>
      <div className={styles.logo} onClick={() => navigate("/")}>
        GAMESTORE
      </div>

      {navLinks.map((link, index) => (
        <div
          key={index}
          className={`${styles.navItem} ${isActive(link.path) ? styles.navItemActive : ''}`}
          onClick={() => link.path !== "#" && navigate(link.path)}
        >
          {link.label}
        </div>
      ))}
    </div>
  );
}
