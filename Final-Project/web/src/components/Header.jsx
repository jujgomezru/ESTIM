import { useState, useEffect, useRef } from "react";
import { useNavigate, useLocation } from "react-router-dom";

export default function Header() {
  const [searchQuery, setSearchQuery] = useState("");
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const navigate = useNavigate();
  const location = useLocation(); // ✅ Hook para saber la ruta actual
  const dropdownRef = useRef(null);

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

  // ✅ Función para verificar si la ruta está activa
  function isActive(path) {
    if (path === "/") {
      return location.pathname === "/" || location.pathname === "/store";
    }
    return location.pathname === path;
  }

  return (
    <header style={styles.header}>
      <div style={styles.navLeft}>
        <div style={styles.logo} onClick={() => navigate("/")}>
          GAMESTORE
        </div>
        
        {/* Store */}
        <div 
          style={{
            ...styles.navItem,
            ...(isActive("/") && styles.navItemActive)
          }}
          onClick={() => navigate("/")}
        >
          Store
        </div>

        {/* Library */}
        <div 
          style={{
            ...styles.navItem,
            ...(isActive("/library") && styles.navItemActive)
          }}
          onClick={() => navigate("/library")}
        >
          Library
        </div>

        {/* Community */}
        <div style={styles.navItem}>
          Community
        </div>

        {/* Deals */}
        <div style={styles.navItem}>
          Deals
        </div>
      </div>

      <div style={styles.navRight}>
        <div style={styles.searchContainer}>
          <input
            type="text"
            placeholder="Search games..."
            style={styles.searchBox}
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
          <span style={styles.searchIcon}>🔍</span>
        </div>
        <div style={styles.iconBtn} onClick={() => navigate("/cart")}>🛒</div>
        
        {/* User Dropdown */}
        <div style={styles.dropdownContainer} ref={dropdownRef}>
          <div style={styles.iconBtn} onClick={toggleDropdown}>
            👤
          </div>
          
          {isDropdownOpen && (
            <div style={styles.dropdownMenu}>
              <div 
                style={styles.dropdownItem}
                onMouseEnter={(e) => e.currentTarget.style.background = 'rgba(255, 107, 53, 0.1)'}
                onMouseLeave={(e) => e.currentTarget.style.background = 'transparent'}
                onClick={() => handleNavigate("/wishlist")}
              >
                💝 Wishlist
              </div>
              
              <div 
                style={styles.dropdownItem}
                onMouseEnter={(e) => e.currentTarget.style.background = 'rgba(255, 107, 53, 0.1)'}
                onMouseLeave={(e) => e.currentTarget.style.background = 'transparent'}
                onClick={() => handleNavigate("/games")}
              >
                🎮 All Games
              </div>

              <div style={styles.dropdownDivider} />

              <div 
                style={styles.dropdownItem}
                onMouseEnter={(e) => e.currentTarget.style.background = 'rgba(255, 107, 53, 0.1)'}
                onMouseLeave={(e) => e.currentTarget.style.background = 'transparent'}
                onClick={() => handleNavigate("/login")}
              >
                🔑 Login
              </div>

              <div 
                style={styles.dropdownItem}
                onMouseEnter={(e) => e.currentTarget.style.background = 'rgba(255, 107, 53, 0.1)'}
                onMouseLeave={(e) => e.currentTarget.style.background = 'transparent'}
                onClick={() => handleNavigate("/register")}
              >
                ✍️ Sign Up
              </div>
            </div>
          )}
        </div>
      </div>
    </header>
  );
}

const styles = {
  header: {
    background: 'rgba(0, 0, 0, 0.95)',
    padding: '12px 40px',
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    borderBottom: '1px solid #222',
    position: 'sticky',
    top: 0,
    zIndex: 1000
  },
  navLeft: {
    display: 'flex',
    gap: '30px',
    alignItems: 'center'
  },
  logo: {
    fontSize: '18px',
    fontWeight: '700',
    color: '#ff6b35',
    letterSpacing: '1px',
    cursor: 'pointer'
  },
  navItem: {
    color: '#aaa',
    fontSize: '14px',
    cursor: 'pointer',
    transition: 'all 0.3s',
    fontWeight: '500',
    position: 'relative',
    padding: '8px 0'
  },
  navItemActive: {
    color: '#ff6b35', // ✅ Color naranja cuando está activo
    fontWeight: '600',
    // ✅ Línea inferior para indicar activo
    borderBottom: '2px solid #ff6b35'
  },
  navRight: {
    display: 'flex',
    gap: '20px',
    alignItems: 'center'
  },
  searchContainer: {
    position: 'relative',
    display: 'flex',
    alignItems: 'center'
  },
  searchBox: {
    background: '#1a1a1a',
    border: '1px solid #333',
    borderRadius: '20px',
    padding: '8px 40px 8px 16px',
    color: '#fff',
    fontSize: '13px',
    width: '280px',
    outline: 'none'
  },
  searchIcon: {
    position: 'absolute',
    right: '15px',
    fontSize: '14px',
    color: '#666'
  },
  iconBtn: {
    fontSize: '18px',
    cursor: 'pointer',
    transition: 'transform 0.2s',
    color: '#fff'
  },
  dropdownContainer: {
    position: 'relative'
  },
  dropdownMenu: {
    position: 'absolute',
    top: '100%',
    right: 0,
    marginTop: '12px',
    background: '#1a1a1a',
    border: '1px solid #333',
    borderRadius: '8px',
    minWidth: '200px',
    boxShadow: '0 8px 24px rgba(0, 0, 0, 0.6)',
    padding: '8px 0',
    zIndex: 1000
  },
  dropdownItem: {
    padding: '12px 20px',
    color: '#ccc',
    fontSize: '14px',
    cursor: 'pointer',
    transition: 'all 0.2s',
    display: 'flex',
    alignItems: 'center',
    gap: '10px',
    background: 'transparent'
  },
  dropdownDivider: {
    height: '1px',
    background: '#333',
    margin: '8px 0'
  }
};
