class CloudflareImageUploader {
    constructor() {
        this.credentials = null;
    }

    // 백엔드에서 인증 정보를 가져오는 메서드
    async initialize() {
        try {
            const response = await fetch('/api/cloudflare/credentials', {
                method: 'GET',
                credentials: 'same-origin' // 쿠키 포함
            });
            
            if (!response.ok) {
                throw new Error('Failed to get Cloudflare credentials');
            }
            
            this.credentials = await response.json();
            this.uploadUrl = `https://api.cloudflare.com/client/v4/accounts/${this.credentials.accountId}/images/v1`;
        } catch (error) {
            console.error('Error initializing CloudflareImageUploader:', error);
            throw error;
        }
    }

    async uploadImage(file) {
        if (!this.credentials) {
            await this.initialize();
        }
        
        try {
            const formData = new FormData();
            formData.append('file', file);

            const response = await fetch(this.uploadUrl, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${this.credentials.apiToken}`
                },
                body: formData
            });

            if (!response.ok) {
                throw new Error('Image upload failed');
            }

            const result = await response.json();
            
            if (result.success) {
                // Cloudflare Images의 응답에서 이미지 URL 추출
                return {
                    success: true,
                    imageUrl: result.result.variants[0], // 기본 변형 URL
                    imageId: result.result.id
                };
            } else {
                throw new Error(result.errors[0].message);
            }
        } catch (error) {
            console.error('Error uploading image:', error);
            return {
                success: false,
                error: error.message
            };
        }
    }
} 