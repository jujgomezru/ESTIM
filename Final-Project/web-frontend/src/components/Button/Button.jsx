import styles from "./Button.module.css";

export default function Button({ 
  children, 
  onClick, 
  variant = "primary", 
  size = "medium",
  icon,
  iconPosition = "left",
  fullWidth = false,
  disabled = false,
  loading = false,
  type = "button",
  style = {}
}) {
  const buttonClasses = [
    styles.button,
    styles[variant],
    styles[size],
    fullWidth && styles.fullWidth,
    disabled && styles.disabled,
    loading && styles.loading
  ].filter(Boolean).join(' ');

  return (
    <button 
      type={type}
      className={buttonClasses}
      style={style}
      onClick={onClick}
      disabled={disabled || loading}
    >
      {loading ? (
        <span className={styles.spinner}>⏳</span>
      ) : (
        <>
          {icon && iconPosition === "left" && (
            <span className={styles.icon}>{icon}</span>
          )}
          {children}
          {icon && iconPosition === "right" && (
            <span className={styles.icon}>{icon}</span>
          )}
        </>
      )}
    </button>
  );
}
