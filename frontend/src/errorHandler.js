// APIエラーハンドル
export function handleApiError(responseJson) {
    const errorResponse = responseJson;
    alert(`Error: ${errorResponse.displayMessage}`);
}