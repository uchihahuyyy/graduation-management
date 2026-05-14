(function () {
    function normalize(value) {
        return (value || '').toString().toLowerCase().trim();
    }

    function applyTableControls(table) {
        var target = table.getAttribute('data-table-target');
        var searchInputs = Array.prototype.slice.call(document.querySelectorAll('[data-search-target="' + target + '"]'));
        var pageSize = document.querySelector('[data-page-size-target="' + target + '"]');
        var rows = Array.prototype.slice.call(table.querySelectorAll('tbody tr[data-row]'));
        var emptyRow = table.querySelector('tbody tr[data-empty-row]');

        function render() {
            if (rows.length === 0) {
                if (emptyRow) {
                    emptyRow.classList.remove('show');
                }
                return;
            }

            var keywords = searchInputs
                .map(function (input) {
                    return normalize(input.value);
                })
                .filter(Boolean);
            var limit = pageSize ? parseInt(pageSize.value, 10) : rows.length;
            var visibleCount = 0;

            rows.forEach(function (row) {
                var rowText = normalize(row.innerText);
                var matched = keywords.every(function (keyword) {
                    return rowText.indexOf(keyword) !== -1;
                });
                var inLimit = visibleCount < limit;
                row.style.display = matched && inLimit ? '' : 'none';
                if (matched) {
                    visibleCount += 1;
                }
            });

            if (emptyRow) {
                var hasVisibleRow = rows.some(function (row) {
                    return row.style.display !== 'none';
                });
                emptyRow.classList.toggle('show', !hasVisibleRow);
            }
        }

        searchInputs.forEach(function (search) {
            search.addEventListener('input', render);
        });
        if (pageSize) {
            pageSize.addEventListener('change', render);
        }
        render();
    }

    document.addEventListener('DOMContentLoaded', function () {
        Array.prototype.forEach.call(document.querySelectorAll('table[data-table-target]'), applyTableControls);
    });
})();
