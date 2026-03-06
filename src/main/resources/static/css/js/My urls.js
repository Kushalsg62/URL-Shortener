document.addEventListener('DOMContentLoaded', function () {
    const selectAll     = document.getElementById('selectAll');
    const rowCheckboxes = document.querySelectorAll('.row-checkbox');
    const deleteBtn     = document.getElementById('deleteSelectedBtn');
    const deleteForm    = document.getElementById('deleteForm');

    if (!selectAll || !deleteBtn || !deleteForm) return;

    function updateDeleteBtn() {
        const anyChecked = Array.from(rowCheckboxes).some(cb => cb.checked);
        deleteBtn.disabled = !anyChecked;
    }

    selectAll.addEventListener('change', function () {
        rowCheckboxes.forEach(cb => cb.checked = this.checked);
        updateDeleteBtn();
    });

    rowCheckboxes.forEach(cb => {
        cb.addEventListener('change', function () {
            updateDeleteBtn();
            selectAll.checked = Array.from(rowCheckboxes).every(cb => cb.checked);
        });
    });

    deleteBtn.addEventListener('click', function () {
        if (confirm('Delete selected URLs? This cannot be undone.')) {
            deleteForm.submit();
        }
    });
});