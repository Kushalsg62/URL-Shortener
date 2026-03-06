const COPY_ICON = '<svg xmlns="http://www.w3.org/2000/svg" width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><rect x="9" y="9" width="13" height="13" rx="2"/><path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"/></svg>';
const CHECK_ICON = '<svg xmlns="http://www.w3.org/2000/svg" width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"/></svg>';

function copyLink(btn) {
    const url = btn.getAttribute('data-url');

    if (navigator.clipboard && window.isSecureContext) {
        navigator.clipboard.writeText(url).then(() => flashCopied(btn));
    } else {
        // Fallback for http or older browsers
        const ta = document.createElement('textarea');
        ta.value = url;
        ta.style.position = 'fixed';
        ta.style.opacity = '0';
        document.body.appendChild(ta);
        ta.focus();
        ta.select();
        document.execCommand('copy');
        document.body.removeChild(ta);
        flashCopied(btn);
    }
}

function flashCopied(btn) {
    btn.classList.add('copied');
    btn.title = 'Copied!';
    btn.innerHTML = CHECK_ICON;
    setTimeout(() => {
        btn.classList.remove('copied');
        btn.title = 'Copy link';
        btn.innerHTML = COPY_ICON;
    }, 2000);
}