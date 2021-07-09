// copy the element text to clipboard for the given element id
function copyToClipboard(id) {
    const copyText = document.getElementById(id).innerText;
    navigator.clipboard.writeText(copyText);
}