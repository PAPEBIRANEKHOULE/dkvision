// ====== DK-VISION Frontend App ======

(function() {
    'use strict';

    // ====== Toast Notification System ======
    function showToast(message, type) {
        type = type || 'info';
        var container = document.getElementById('toast-container');
        if (!container) {
            container = document.createElement('div');
            container.id = 'toast-container';
            container.style.cssText = 'position:fixed;top:20px;right:20px;z-index:9999;display:flex;flex-direction:column;gap:10px';
            document.body.appendChild(container);
        }

        var toast = document.createElement('div');
        toast.className = 'toast toast-' + type;
        var icons = { success: '✓', error: '✕', info: 'ℹ', warning: '⚠' };
        toast.innerHTML = '<span class="toast-icon">' + (icons[type] || '') + '</span><span class="toast-msg">' + message + '</span>';
        toast.style.cssText = 'display:flex;align-items:center;gap:10px;background:#fff;color:#333;padding:14px 20px;border-radius:12px;box-shadow:0 10px 40px rgba(0,0,0,0.15);transform:translateX(120%);opacity:0;transition:all 0.4s cubic-bezier(0.68,-0.55,0.265,1.35);min-width:280px;max-width:420px;border-left:4px solid #e94560;font-size:14px';
        toast.querySelector('.toast-icon').style.cssText = 'font-size:18px;font-weight:700;width:28px;height:28px;display:flex;align-items:center;justify-content:center;border-radius:50%;flex-shrink:0';

        var borderColors = { success: '#10b981', error: '#ef4444', info: '#3b82f6', warning: '#f59e0b' };
        toast.style.borderLeftColor = borderColors[type] || borderColors.info;
        var iconEl = toast.querySelector('.toast-icon');
        iconEl.style.background = (borderColors[type] || borderColors.info) + '22';
        iconEl.style.color = borderColors[type] || borderColors.info;

        container.appendChild(toast);

        requestAnimationFrame(function() {
            toast.style.transform = 'translateX(0)';
            toast.style.opacity = '1';
        });

        setTimeout(function() {
            toast.style.transform = 'translateX(120%)';
            toast.style.opacity = '0';
            setTimeout(function() { toast.remove(); }, 400);
        }, 3500);
    }

    // ====== Enhanced Lightbox ======
    function initLightbox() {
        var modal = document.getElementById('photoModal');
        if (!modal) return;

        var modalImg = document.getElementById('modalImage');
        var caption = document.getElementById('modalCaption');
        var closeBtn = modal.querySelector('.modal-close');
        var prevBtn = modal.querySelector('.modal-prev');
        var nextBtn = modal.querySelector('.modal-next');
        var currentIndex = -1;
        var images = [];

        function updateImages() {
            images = Array.from(document.querySelectorAll('.photo-wrapper img'));
        }

        // Event delegation on photo grid
        document.addEventListener('click', function(e) {
            var img = e.target.closest('.photo-wrapper img');
            if (!img) return;
            var wrapper = img.closest('.photo-wrapper');
            if (!wrapper) return;
            var deleteBtn = wrapper.querySelector('.btn-delete');
            if (deleteBtn && deleteBtn.contains(e.target)) return;

            updateImages();
            var index = parseInt(img.getAttribute('data-index')) || 0;
            var src = img.src;
            var alt = img.getAttribute('data-title') || img.alt || '';
            currentIndex = index;
            modal.style.display = 'flex';
            modalImg.src = src;
            caption.textContent = alt;
            document.body.style.overflow = 'hidden';
            updateNavButtons();
            modalImg.style.transform = 'scale(1)';
        });

        window.closeModal = function() {
            modal.style.display = 'none';
            document.body.style.overflow = 'auto';
        };

        window.closeModal = function() {
            modal.style.display = 'none';
            document.body.style.overflow = 'auto';
        };

        function showImage(index) {
            if (index < 0 || index >= images.length) return;
            currentIndex = index;
            var img = images[index];
            modalImg.src = img.src;
            var alt = img.alt || img.getAttribute('data-title') || '';
            caption.textContent = alt;
            updateNavButtons();
            modalImg.style.transform = 'scale(1)';
        }

        function updateNavButtons() {
            if (prevBtn) prevBtn.style.display = currentIndex > 0 ? 'flex' : 'none';
            if (nextBtn) nextBtn.style.display = currentIndex < images.length - 1 ? 'flex' : 'none';

            if (document.getElementById('photoCounter')) {
                document.getElementById('photoCounter').textContent = (currentIndex + 1) + ' / ' + images.length;
            }
        }

        if (prevBtn) prevBtn.addEventListener('click', function(e) {
            e.stopPropagation();
            if (currentIndex > 0) showImage(currentIndex - 1);
        });

        if (nextBtn) nextBtn.addEventListener('click', function(e) {
            e.stopPropagation();
            if (currentIndex < images.length - 1) showImage(currentIndex + 1);
        });

        document.addEventListener('keydown', function(e) {
            if (modal.style.display !== 'flex') return;
            if (e.key === 'Escape') closeModal();
            if (e.key === 'ArrowLeft' && currentIndex > 0) showImage(currentIndex - 1);
            if (e.key === 'ArrowRight' && currentIndex < images.length - 1) showImage(currentIndex + 1);
        });

        modal.addEventListener('click', function(e) {
            if (e.target === modal) closeModal();
        });

        // Click zoom on image
        modalImg.addEventListener('click', function() {
            var currentScale = modalImg.style.transform === 'scale(1.5)' ? 1 : 1.5;
            modalImg.style.transform = 'scale(' + currentScale + ')';
            modalImg.style.transition = 'transform 0.3s ease';
        });

        // Re-fetch images after dynamic content changes
        var observer = new MutationObserver(function() { updateImages(); });
        var photoGrid = document.querySelector('.photo-grid');
        if (photoGrid) observer.observe(photoGrid, { childList: true, subtree: true });
    }

    // ====== Lazy Loading Images ======
    function initLazyLoading() {
        if ('IntersectionObserver' in window) {
            var lazyImages = document.querySelectorAll('img[data-src]');
            var observer = new IntersectionObserver(function(entries) {
                entries.forEach(function(entry) {
                    if (entry.isIntersecting) {
                        var img = entry.target;
                        img.src = img.dataset.src;
                        img.removeAttribute('data-src');
                        observer.unobserve(img);
                    }
                });
            }, { rootMargin: '200px 0px' });

            lazyImages.forEach(function(img) { observer.observe(img); });
        }
    }

    // ====== Scroll Reveal Animations ======
    function initScrollReveal() {
        if (!('IntersectionObserver' in window)) return;
        var elements = document.querySelectorAll('.reveal');
        var observer = new IntersectionObserver(function(entries) {
            entries.forEach(function(entry) {
                if (entry.isIntersecting) {
                    entry.target.classList.add('revealed');
                    observer.unobserve(entry.target);
                }
            });
        }, { threshold: 0.1, rootMargin: '0px 0px -50px 0px' });

        elements.forEach(function(el) { observer.observe(el); });
    }

    // ====== Drag & Drop Upload ======
    function initDragDrop() {
        var dropZone = document.getElementById('drop-zone');
        var fileInput = document.getElementById('file');
        var previewContainer = document.getElementById('upload-preview');
        if (!dropZone || !fileInput) return;

        ['dragenter', 'dragover', 'dragleave', 'drop'].forEach(function(event) {
            dropZone.addEventListener(event, function(e) {
                e.preventDefault();
                e.stopPropagation();
            });
        });

        ['dragenter', 'dragover'].forEach(function(event) {
            dropZone.addEventListener(event, function() {
                dropZone.classList.add('drag-over');
            });
        });

        ['dragleave', 'drop'].forEach(function(event) {
            dropZone.addEventListener(event, function() {
                dropZone.classList.remove('drag-over');
            });
        });

        dropZone.addEventListener('drop', function(e) {
            var files = e.dataTransfer.files;
            if (files.length) handleFiles(files);
        });

        dropZone.addEventListener('click', function() { fileInput.click(); });

        fileInput.addEventListener('change', function(e) {
            if (this.files.length) {
                handleFiles(this.files);
                // Update file input name display
                var nameEl = document.querySelector('.file-name');
                if (nameEl) {
                    nameEl.textContent = this.files.length > 1
                        ? this.files.length + ' fichiers sélectionnés'
                        : this.files[0].name;
                }
            }
        });

        function handleFiles(files) {
            if (!previewContainer) return;
            previewContainer.innerHTML = '';
            previewContainer.style.display = 'flex';

            // Enable multi-file by modifying form
            var form = fileInput.closest('form');
            if (form) {
                form.querySelector('button[type="submit"]').textContent = 'Uploader ' + files.length + ' fichier(s)';
            }

            Array.from(files).forEach(function(file) {
                if (!file.type.startsWith('image/')) return;
                var reader = new FileReader();
                var preview = document.createElement('div');
                preview.className = 'upload-preview-item reveal';

                reader.onload = function(e) {
                    preview.innerHTML = '<img src="' + e.target.result + '" alt="' + file.name + '">'
                        + '<span class="preview-name">' + file.name + '</span>'
                        + '<span class="preview-size">' + formatFileSize(file.size) + '</span>';
                    previewContainer.appendChild(preview);
                    requestAnimationFrame(function() { preview.classList.add('revealed'); });
                };
                reader.readAsDataURL(file);
            });
        }

        // Add progress bar
        var form = fileInput.closest('form');
        if (form) {
            var progressContainer = document.createElement('div');
            progressContainer.id = 'upload-progress';
            progressContainer.style.display = 'none';
            progressContainer.innerHTML = '<div class="progress-bar"><div class="progress-fill"></div></div><span class="progress-text">0%</span>';
            form.querySelector('.form-actions').before(progressContainer);
        }
    }

    // ====== Upload Progress ======
    function initUploadProgress() {
        var form = document.querySelector('form[enctype="multipart/form-data"]');
        if (!form) return;

        form.addEventListener('submit', function(e) {
            // For normal form submission we can't track progress
            // Just show a loading state
            var btn = form.querySelector('button[type="submit"]');
            if (btn) {
                btn.disabled = true;
                btn.innerHTML = '<span class="spinner"></span> Upload en cours...';
            }
            showToast('Upload en cours...', 'info');

            // Show progress bar animation
            var progress = document.getElementById('upload-progress');
            if (progress) {
                progress.style.display = 'flex';
                var fill = progress.querySelector('.progress-fill');
                var text = progress.querySelector('.progress-text');
                if (fill && text) {
                    var width = 0;
                    var interval = setInterval(function() {
                        if (width >= 90) { clearInterval(interval); return; }
                        width += Math.random() * 15;
                        if (width > 90) width = 90;
                        fill.style.width = width + '%';
                        text.textContent = Math.round(width) + '%';
                    }, 300);
                    // Store interval to clear after page loads
                    form.dataset.progressInterval = interval;
                }
            }
        });
    }

    // ====== Gallery Search / Filter ======
    function initGallerySearch() {
        var searchInput = document.getElementById('gallery-search');
        var galleryCards = document.querySelectorAll('.gallery-card');
        if (!searchInput || !galleryCards.length) return;

        searchInput.addEventListener('input', function() {
            var query = this.value.toLowerCase().trim();
            galleryCards.forEach(function(card) {
                var name = (card.querySelector('h4') || {}).textContent || '';
                var desc = (card.querySelector('.description') || {}).textContent || '';
                var match = name.toLowerCase().includes(query) || desc.toLowerCase().includes(query);
                card.style.display = match ? '' : 'none';
            });

            var visible = Array.from(galleryCards).filter(function(c) { return c.style.display !== 'none'; });
            var emptyMsg = document.getElementById('search-empty');
            if (emptyMsg) {
                emptyMsg.style.display = visible.length === 0 ? 'block' : 'none';
            }
        });
    }

    // ====== Confirm Dialogs ======
    function initConfirmDialogs() {
        document.querySelectorAll('[data-confirm]').forEach(function(el) {
            el.addEventListener('click', function(e) {
                if (!confirm(this.dataset.confirm)) e.preventDefault();
            });
        });
    }

    // ====== Utility Functions ======
    function formatFileSize(bytes) {
        if (bytes < 1024) return bytes + ' o';
        if (bytes < 1048576) return (bytes / 1024).toFixed(1) + ' Ko';
        return (bytes / 1048576).toFixed(1) + ' Mo';
    }

    // ====== Init ======
    document.addEventListener('DOMContentLoaded', function() {
        initLightbox();
        initLazyLoading();
        initScrollReveal();
        initDragDrop();
        initUploadProgress();
        initGallerySearch();
        initConfirmDialogs();

        // Add reveal class to gallery cards for scroll animation
        document.querySelectorAll('.gallery-card, .photo-item, .hero').forEach(function(el) {
            if (!el.classList.contains('reveal')) el.classList.add('reveal');
        });

        // Set active nav link
        var currentPath = window.location.pathname;
        document.querySelectorAll('nav a').forEach(function(a) {
            a.classList.remove('active');
            if (a.getAttribute('href') === currentPath) a.classList.add('active');
        });
    });

    // Expose toast globally
    window.showToast = showToast;

})();
