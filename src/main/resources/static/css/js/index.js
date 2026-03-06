document.addEventListener('DOMContentLoaded', function () {
    const privateToggle    = document.getElementById('isPrivate');
    const privateLabelText = document.getElementById('privateLabelText');

    function updatePrivateLabel() {
        if (!privateToggle || !privateLabelText) return;
        if (privateToggle.checked) {
            privateLabelText.style.color      = 'var(--accent)';
            privateLabelText.style.fontWeight = '600';
        } else {
            privateLabelText.style.color      = 'var(--text-muted)';
            privateLabelText.style.fontWeight = '400';
        }
    }

    if (privateToggle) {
        privateToggle.addEventListener('change', updatePrivateLabel);
        updatePrivateLabel(); // reflect server-side state on page load
    }
});