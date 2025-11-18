export interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  stock: number;
  categoryId: number;
  categoryName: string;
  imageFilename: string | null;
  imageUrl: string | null;
  isOrganic: boolean;
  certifications: string | null;
  originCountry: string | null;
  createdAt: Date;
  updatedAt: Date;
  // Additional frontend fields (ensure defined for template bindings)
  category: string;
  inStock: boolean;
  ecoRating: number;
  sustainabilityScore: number;
  carbonFootprint: number;
  tags: string[];
}