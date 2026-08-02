(function () {
    const themeToggle = document.getElementById('themeToggle');
    if (themeToggle) {
        themeToggle.addEventListener('click', () => {
            const html = document.documentElement;
            const next = html.getAttribute('data-bs-theme') === 'dark' ? 'light' : 'dark';
            html.setAttribute('data-bs-theme', next);
            localStorage.setItem('nexus-theme', next);
            updateThemeIcon();
        });
        updateThemeIcon();
    }

    function updateThemeIcon() {
        if (!themeToggle) return;
        const dark = document.documentElement.getAttribute('data-bs-theme') === 'dark';
        themeToggle.innerHTML = dark
            ? '<i class="bi bi-sun"></i>'
            : '<i class="bi bi-moon-stars"></i>';
    }

    document.querySelectorAll('.like-btn').forEach((btn) => {
        btn.addEventListener('click', async () => {
            const postId = btn.dataset.postId;
            if (!postId || btn.disabled) return;
            btn.disabled = true;
            try {
                const res = await fetch(`/api/posts/${postId}/like`, {
                    method: 'POST',
                    headers: {
                        'Accept': 'application/json',
                        'X-Requested-With': 'XMLHttpRequest'
                    }
                });
                if (!res.ok) throw new Error('Like failed');
                const data = await res.json();
                btn.dataset.liked = String(data.liked);
                btn.classList.toggle('btn-danger', data.liked);
                btn.classList.toggle('btn-outline-secondary', !data.liked);
                const icon = btn.querySelector('i');
                icon.classList.toggle('bi-heart-fill', data.liked);
                icon.classList.toggle('bi-heart', !data.liked);
                const count = btn.querySelector('.like-count');
                if (count) count.textContent = data.likeCount;
            } catch (e) {
                console.error(e);
            } finally {
                btn.disabled = false;
            }
        });
    });

    document.querySelectorAll('.comment-form').forEach((form) => {
        form.addEventListener('submit', async (event) => {
            event.preventDefault();
            const postId = form.dataset.postId;
            const input = form.querySelector('input[name="content"]');
            const content = (input?.value || '').trim();
            if (!content) return;

            const body = new URLSearchParams();
            body.set('content', content);

            try {
                const res = await fetch(`/api/posts/${postId}/comments`, {
                    method: 'POST',
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/x-www-form-urlencoded',
                        'X-Requested-With': 'XMLHttpRequest'
                    },
                    body
                });
                if (!res.ok) throw new Error('Comment failed');
                const comment = await res.json();
                appendComment(postId, comment);
                input.value = '';
                const countEl = form.closest('.card-body')?.querySelector('.comment-count');
                if (countEl) {
                    countEl.textContent = String(Number(countEl.textContent || '0') + 1);
                }
            } catch (e) {
                console.error(e);
                alert('Could not add comment');
            }
        });
    });

    document.addEventListener('click', async (event) => {
        const btn = event.target.closest('.delete-comment-btn');
        if (!btn) return;
        const commentId = btn.dataset.commentId;
        if (!commentId || !confirm('Delete this comment?')) return;
        try {
            const res = await fetch(`/api/comments/${commentId}/delete`, {
                method: 'POST',
                headers: {
                    'Accept': 'application/json',
                    'X-Requested-With': 'XMLHttpRequest'
                }
            });
            if (!res.ok) throw new Error('Delete failed');
            const item = btn.closest('.comment-item');
            const card = btn.closest('.card-body');
            item?.remove();
            const countEl = card?.querySelector('.comment-count');
            if (countEl) {
                countEl.textContent = String(Math.max(0, Number(countEl.textContent || '0') - 1));
            }
        } catch (e) {
            console.error(e);
            alert('Could not delete comment');
        }
    });

    function appendComment(postId, comment) {
        const list = document.querySelector(`.comments[data-post-id="${postId}"] .comment-list`);
        if (!list) return;
        const initial = (comment.authorUsername || '?').charAt(0).toUpperCase();
        const avatar = comment.authorAvatarPath
            ? `<img src="${escapeHtml(comment.authorAvatarPath)}" alt="${escapeHtml(comment.authorUsername)}" class="avatar-img">`
            : `<div class="avatar-fallback">${escapeHtml(initial)}</div>`;
        const wrapper = document.createElement('div');
        wrapper.className = 'comment-item d-flex gap-2 mb-2';
        wrapper.dataset.commentId = comment.id;
        wrapper.innerHTML = `
            <div class="avatar-wrap avatar-sm">${avatar}</div>
            <div class="flex-grow-1">
                <div class="bg-body-secondary rounded-3 px-3 py-2">
                    <a class="fw-semibold small text-decoration-none" href="/profile/${encodeURIComponent(comment.authorUsername)}">${escapeHtml(comment.authorUsername)}</a>
                    <span class="small">${escapeHtml(comment.content)}</span>
                </div>
                <div class="d-flex align-items-center gap-2 mt-1">
                    <span class="text-secondary" style="font-size: .75rem;">just now</span>
                    <button type="button" class="btn btn-link btn-sm text-danger p-0 delete-comment-btn" data-comment-id="${comment.id}">Delete</button>
                </div>
            </div>`;
        list.appendChild(wrapper);
    }

    function escapeHtml(value) {
        return String(value ?? '')
            .replaceAll('&', '&amp;')
            .replaceAll('<', '&lt;')
            .replaceAll('>', '&gt;')
            .replaceAll('"', '&quot;')
            .replaceAll("'", '&#39;');
    }

    const imageInput = document.querySelector('input[type="file"][data-preview]');
    const preview = document.getElementById('imagePreview');
    if (imageInput && preview) {
        imageInput.addEventListener('change', () => {
            const file = imageInput.files?.[0];
            if (!file) {
                preview.classList.add('d-none');
                preview.removeAttribute('src');
                return;
            }
            const url = URL.createObjectURL(file);
            preview.src = url;
            preview.classList.remove('d-none');
        });
    }
})();
