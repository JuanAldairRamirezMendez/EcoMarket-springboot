import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { BehaviorSubject, Observable, of, catchError, map } from 'rxjs';
import { Product } from '../models/product.model';
import { environment } from '../../environments/environment';

interface ProductResponse {
  id: number;
  name: string;
  description: string;
  price: number;
  stock: number;
  categoryId?: number;
  categoryName?: string;
  category?: string;
  imageFilename?: string | null;
  imageUrl?: string | null;
  isOrganic?: boolean;
  certifications?: string | null;
  originCountry?: string | null;
  createdAt?: string;
  updatedAt?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ProductsService {
  private readonly API_URL = `${environment.apiUrl}/products`;
  private products: Product[] = [];
  private productsSubject = new BehaviorSubject<Product[]>(this.products);
  public products$ = this.productsSubject.asObservable();

  constructor(private http: HttpClient) {
    this.loadProducts();
  }

  private loadProducts(): void {
    this.http.get<{ content: ProductResponse[] }>(this.API_URL, {
      params: new HttpParams().set('size', '100')
    }).pipe(
      catchError((error) => {
        console.error('Error loading products:', error);
        return of({ content: [] as ProductResponse[] });
      }),
      map((response: { content: ProductResponse[] }) => response.content.map(this.mapToProduct.bind(this)))
    ).subscribe((products: Product[]) => {
      this.products = products;
      this.productsSubject.next([...this.products]);
    });
  }

  getProducts(): Observable<Product[]> {
    return this.http.get<{ content: ProductResponse[] }>(this.API_URL, {
      params: new HttpParams().set('size', '100')
    }).pipe(
      catchError((error) => {
        console.error('Error getting products:', error);
        return of({ content: [] as ProductResponse[] });
      }),
      map((response: { content: ProductResponse[] }) => response.content.map(this.mapToProduct.bind(this)))
    );
  }

  getProductById(id: number): Observable<Product | undefined> {
    return this.http.get<ProductResponse>(`${this.API_URL}/${id}`).pipe(
      catchError((error) => {
        console.error('Error getting product by id:', error);
        return of(undefined);
      }),
      map((response: ProductResponse | undefined) => response ? this.mapToProduct(response) : undefined)
    );
  }

  getProductsByCategory(categoryName: string): Observable<Product[]> {
    return this.http.get<{ content: ProductResponse[] }>(this.API_URL, {
      params: new HttpParams()
        .set('category', categoryName)
        .set('size', '100')
    }).pipe(
      catchError((error) => {
        console.error('Error getting products by category:', error);
        return of({ content: [] as ProductResponse[] });
      }),
      map((response: { content: ProductResponse[] }) => response.content.map(this.mapToProduct.bind(this)))
    );
  }

  searchProducts(query: string): Observable<Product[]> {
    return this.http.get<{ content: ProductResponse[] }>(this.API_URL, {
      params: new HttpParams()
        .set('search', query)
        .set('size', '100')
    }).pipe(
      catchError((error) => {
        console.error('Error searching products:', error);
        return of({ content: [] as ProductResponse[] });
      }),
      map((response: { content: ProductResponse[] }) => response.content.map(this.mapToProduct.bind(this)))
    );
  }

  getFeatured(): Product[] {
    return this.products.slice(0, 3);
  }

  getCategories(): string[] {
    return [...new Set(this.products.map(p => p.categoryName))];
  }

  private mapToProduct(response: ProductResponse): Product {
    const imageUrl = response.imageUrl || (response.imageFilename
      ? `${environment.apiUrl}/images/${response.imageFilename}`
      : 'https://images.unsplash.com/photo-1542838132-92c53300491e?w=400&h=300&fit=crop');

    return {
      id: response.id,
      name: response.name,
      description: response.description,
      price: response.price,
      stock: response.stock ?? 0,
      categoryId: response.categoryId ?? 0,
      categoryName: response.categoryName ?? response.category ?? 'Uncategorized',
      category: response.categoryName ?? response.category ?? 'Uncategorized',
      imageFilename: response.imageFilename ?? null,
      imageUrl: imageUrl,
      isOrganic: response.isOrganic ?? false,
      certifications: response.certifications ?? null,
      originCountry: response.originCountry ?? null,
      tags: (response as any).tags ?? [],
      ecoRating: (response as any).ecoRating ?? 0,
      sustainabilityScore: (response as any).sustainabilityScore ?? 0,
      carbonFootprint: (response as any).carbonFootprint ?? 0,
      inStock: (response as any).inStock ?? (response.stock ? response.stock > 0 : true),
      createdAt: response.createdAt ? new Date(response.createdAt) : new Date(),
      updatedAt: response.updatedAt ? new Date(response.updatedAt) : new Date()
    } as Product;
  }

  private mapToProductResponse(product: Product): ProductResponse {
    return {
      id: product.id,
      name: product.name,
      description: product.description,
      price: product.price,
      stock: product.stock,
      categoryId: product.categoryId,
      categoryName: product.categoryName,
      imageFilename: product.imageFilename ?? null,
      imageUrl: product.imageUrl ?? null,
      createdAt: product.createdAt.toISOString(),
      updatedAt: product.updatedAt.toISOString()
    };
  }

  // Basic create/update/delete implementations to satisfy components (use API if available)
  createProduct(product: Omit<Product, 'id' | 'createdAt' | 'updatedAt'>): Observable<Product> {
    const newProduct: Product = {
      ...product as Product,
      id: Date.now(),
      createdAt: new Date(),
      updatedAt: new Date()
    } as Product;
    this.products.push(newProduct);
    this.productsSubject.next([...this.products]);
    return of(newProduct);
  }

  updateProduct(id: number, updates: Partial<Product>): Observable<Product | null> {
    const index = this.products.findIndex(p => p.id === id);
    if (index === -1) return of(null);
    this.products[index] = { ...this.products[index], ...updates, updatedAt: new Date() } as Product;
    this.productsSubject.next([...this.products]);
    return of(this.products[index]);
  }

  deleteProduct(id: number): Observable<boolean> {
    const index = this.products.findIndex(p => p.id === id);
    if (index === -1) return of(false);
    this.products.splice(index, 1);
    this.productsSubject.next([...this.products]);
    return of(true);
  }

  private getMockProducts(): ProductResponse[] {
    return [
      {
        id: 1,
        name: 'Bolsa de Tela Reciclada',
        description: 'Bolsa resistente hecha de materiales reciclados, perfecta para compras ecológicas.',
        price: 25.99,
        stock: 10,
        categoryName: 'Accesorios',
        imageUrl: 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=400&h=300&fit=crop',
        imageFilename: null,
        isOrganic: true,
        certifications: null,
        originCountry: null,
        createdAt: '2024-01-01T00:00:00Z',
        updatedAt: '2024-01-01T00:00:00Z'
      },
      {
        id: 2,
        name: 'Maceta de Plástico Reciclado',
        description: 'Maceta biodegradable para tus plantas, hecha 100% de plástico reciclado.',
        price: 15.50,
        stock: 20,
        categoryName: 'Jardinería',
        imageUrl: 'https://images.unsplash.com/photo-1416879595882-3373a0480b5b?w=400&h=300&fit=crop',
        imageFilename: null,
        isOrganic: false,
        certifications: null,
        originCountry: null,
        createdAt: '2024-01-01T00:00:00Z',
        updatedAt: '2024-01-01T00:00:00Z'
      }
    ];
  }
}
