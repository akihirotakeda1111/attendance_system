import { Component } from "react";

class ErrorBoundary extends Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, navigated: false };
  }

  static getDerivedStateFromError(error) {
    return { hasError: true };
  }

  componentDidCatch(error, errorInfo) {
    console.error("error:", error, errorInfo);
  }

  componentDidUpdate() {
    if ((this.state.hasError || this.props.error) && !this.state.navigated) {
      this.setState({ navigated: true });
      window.location.href = "/error";
    }
  }
  
  render() {
    if (this.state.hasError || this.props.error) {
      return null;
    }
    return this.props.children;
  }
}

export default ErrorBoundary;