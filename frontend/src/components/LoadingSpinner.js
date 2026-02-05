import "./LoadingSpinner.css";

function LoadingSpinner({ message = "로딩 중..." }) {
  return (
    <div className="loading-spinner-container">
      <div className="spinner"></div>
      {message && <p className="loading-spinner-text">{message}</p>}
    </div>
  );
}

export default LoadingSpinner;
