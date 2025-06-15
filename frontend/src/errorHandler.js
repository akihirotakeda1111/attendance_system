// APIエラーハンドル
export function handleApiError(responseJson) {
    const errorResponse = responseJson;
    if (!errorResponse.displayMessage) {
        window.location.href = "/error";
    } else {
        alert(`Error: ${errorResponse.displayMessage}`);
    }
}