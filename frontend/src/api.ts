export type Receipt = {
  id: string;
  merchantName: string;
  purchasedAt: string;
  total: number;
  category: string;
  projectId: string | null;
  status: "NEEDS_REVIEW" | "SAVED";
  receiptNumber: string | null;
  productName: string | null;
  brand: string | null;
  modelNumber: string | null;
  serialNumber: string | null;
  sku: string | null;
  quantity: number | null;
  warrantyProvider: string | null;
  warrantyDurationMonths: number | null;
  warrantyStartAt: string | null;
  warrantyEndAt: string | null;
  returnWindowEndAt: string | null;
  warrantyConfirmed: boolean;
  warrantyStatus: "ACTIVE" | "EXPIRING_SOON" | "EXPIRED" | "UNKNOWN" | "NO_WARRANTY";
};

type CsrfData = { headerName: string; token: string };
let csrfData: CsrfData | null = null;

async function csrfHeaders(json = false): Promise<Record<string, string>> {
  if (!csrfData) {
    const response = await fetch("/api/auth/csrf");
    if (!response.ok) throw new Error("Could not initialize secure session");
    csrfData = await response.json() as CsrfData;
  }
  return {
    [csrfData.headerName]: csrfData.token,
    ...(json ? { "Content-Type": "application/json" } : {})
  };
}

export async function listReceipts(query = "", status = "ALL", sort = "newest"): Promise<Receipt[]> {
  const params = new URLSearchParams();
  if (query) params.set("q", query);
  if (status) params.set("status", status);
  params.set("sort", sort);
  const response = await fetch(`/api/receipts?${params.toString()}`);
  if (!response.ok) throw new Error("Receipt request failed");
  return await response.json() as Receipt[];
}

export type CreateReceipt = {
  merchantName: string;
  purchasedAt: string;
  total: number;
  category: string;
  receiptNumber?: string;
  productName?: string;
  brand?: string;
  modelNumber?: string;
  serialNumber?: string;
  sku?: string;
};

export async function createReceipt(receipt: CreateReceipt): Promise<Receipt> {
  const headers = await csrfHeaders(true);
  const response = await fetch("/api/receipts", {
    method: "POST",
    headers,
    body: JSON.stringify(receipt)
  });
  if (!response.ok) throw new Error("Could not save receipt");
  return await response.json() as Receipt;
}

export async function uploadReceipt(file: File, receipt: CreateReceipt): Promise<Receipt> {
  const form = new FormData();
  form.append("file", file);
  form.append("merchantName", receipt.merchantName);
  form.append("purchasedAt", receipt.purchasedAt);
  form.append("total", String(receipt.total));
  form.append("category", receipt.category);
  form.append("receiptNumber", receipt.receiptNumber ?? "");
  form.append("modelNumber", receipt.modelNumber ?? "");
  form.append("serialNumber", receipt.serialNumber ?? "");
  const response = await fetch("/api/receipts/upload", { method: "POST", headers: await csrfHeaders(), body: form });
  if (!response.ok) throw new Error("Could not upload receipt");
  return await response.json() as Receipt;
}

export type Account = { id: string; email: string; displayName: string };

export async function getAccount(): Promise<Account> {
  const response = await fetch("/api/auth/me");
  if (response.status === 401) throw new Error("UNAUTHENTICATED");
  if (!response.ok) throw new Error("Could not load account");
  return await response.json() as Account;
}

export async function login(email: string, password: string): Promise<Account> {
  const headers = await csrfHeaders(true);
  const response = await fetch("/api/auth/login", {
    method: "POST",
    headers,
    body: JSON.stringify({ email, password })
  });
  if (!response.ok) throw new Error(response.status === 401 ? "Email or password is incorrect" : "Could not sign in");
  csrfData = null;
  return await response.json() as Account;
}

export async function register(email: string, displayName: string, password: string): Promise<Account> {
  const headers = await csrfHeaders(true);
  const response = await fetch("/api/auth/register", {
    method: "POST",
    headers,
    body: JSON.stringify({ email, displayName, password })
  });
  if (!response.ok) throw new Error(response.status === 409 ? "An account with that email already exists" : "Could not create account");
  csrfData = null;
  return await response.json() as Account;
}

export async function loginDemo(): Promise<Account> {
  const headers = await csrfHeaders(true);
  const response = await fetch("/api/auth/demo", {
    method: "POST",
    headers,
    body: "{}"
  });
  if (!response.ok) throw new Error("Could not open the demo account");
  csrfData = null;
  return await response.json() as Account;
}

export async function logout(): Promise<void> {
  await fetch("/api/auth/logout", { method: "POST", headers: await csrfHeaders() });
  csrfData = null;
}

export type Merchant = { name: string; receiptCount: number; total: number; latest: string | null };
export type Project = { id: string; name: string; color: string; receiptCount: number; total: number };
export type HistoryItem = { receiptId: string; eventType: string; description: string; createdAt: string };

export async function listMerchants(): Promise<Merchant[]> {
  const response = await fetch("/api/merchants");
  if (!response.ok) throw new Error("Could not load companies");
  return await response.json() as Merchant[];
}

export async function listProjects(): Promise<Project[]> {
  const response = await fetch("/api/projects");
  if (!response.ok) throw new Error("Could not load projects");
  return await response.json() as Project[];
}

export async function createProject(name: string, color = "blue"): Promise<Project> {
  const headers = await csrfHeaders(true);
  const response = await fetch("/api/projects", {
    method: "POST",
    headers,
    body: JSON.stringify({ name, color })
  });
  if (!response.ok) throw new Error("Could not create project");
  return await response.json() as Project;
}

export async function listHistory(): Promise<HistoryItem[]> {
  const response = await fetch("/api/history");
  if (!response.ok) throw new Error("Could not load history");
  return await response.json() as HistoryItem[];
}

export async function deleteAllReceipts(): Promise<void> {
  const response = await fetch("/api/receipts", { method: "DELETE", headers: await csrfHeaders() });
  if (!response.ok) throw new Error("Could not delete receipt data");
}

export async function deleteAccount(): Promise<void> {
  const response = await fetch("/api/auth/account", { method: "DELETE", headers: await csrfHeaders() });
  if (!response.ok) throw new Error("Could not delete account");
  csrfData = null;
}

export type Duplicate = { receiptId: string; reason: string };

export async function getReceipt(id: string): Promise<Receipt> {
  const response = await fetch(`/api/receipts/${id}`);
  if (!response.ok) throw new Error("Could not load receipt");
  return await response.json() as Receipt;
}

export async function updateReceipt(id: string, receipt: CreateReceipt & {
  projectId?: string;
  receiptNumber?: string;
  productName?: string;
  brand?: string;
  modelNumber?: string;
  serialNumber?: string;
  sku?: string;
  quantity?: number;
  warrantyProvider?: string;
  warrantyDurationMonths?: number;
  warrantyStartAt?: string;
  warrantyEndAt?: string;
  returnWindowEndAt?: string;
  warrantyConfirmed?: boolean;
}): Promise<Receipt> {
  const headers = await csrfHeaders(true);
  const response = await fetch(`/api/receipts/${id}`, {
    method: "PATCH",
    headers,
    body: JSON.stringify({ ...receipt, projectId: receipt.projectId || null })
  });
  if (!response.ok) throw new Error("Could not update receipt");
  return await response.json() as Receipt;
}

export async function deleteReceipt(id: string): Promise<void> {
  const response = await fetch(`/api/receipts/${id}`, { method: "DELETE", headers: await csrfHeaders() });
  if (!response.ok) throw new Error("Could not delete receipt");
}

export async function getDuplicates(id: string): Promise<Duplicate[]> {
  const response = await fetch(`/api/receipts/${id}/duplicates`);
  if (!response.ok) throw new Error("Could not check duplicates");
  return await response.json() as Duplicate[];
}

export type PurchaseDocument = { id: string; documentType: string; mimeType: string; sizeBytes: number };

export async function listPurchaseDocuments(receiptId: string): Promise<PurchaseDocument[]> {
  const response = await fetch(`/api/purchases/${receiptId}/documents`);
  if (!response.ok) throw new Error("Could not load purchase documents");
  return await response.json() as PurchaseDocument[];
}

export async function uploadPurchaseDocument(receiptId: string, file: File, documentType: string): Promise<PurchaseDocument> {
  const form = new FormData();
  form.append("file", file);
  form.append("documentType", documentType);
  const response = await fetch(`/api/purchases/${receiptId}/documents`, {
    method: "POST",
    headers: await csrfHeaders(),
    body: form
  });
  if (!response.ok) throw new Error("Could not upload purchase document");
  return await response.json() as PurchaseDocument;
}

export async function deletePurchaseDocument(receiptId: string, documentId: string): Promise<void> {
  const response = await fetch(`/api/purchases/${receiptId}/documents/${documentId}`, {
    method: "DELETE",
    headers: await csrfHeaders()
  });
  if (!response.ok) throw new Error("Could not delete purchase document");
}

export type WarrantyReminder = { id: string; receiptId: string; reminderAt: string; intervalLabel: string };

export async function listWarrantyReminders(): Promise<WarrantyReminder[]> {
  const response = await fetch("/api/reminders");
  if (!response.ok) throw new Error("Could not load reminders");
  return await response.json() as WarrantyReminder[];
}

export async function createWarrantyReminder(receiptId: string, reminderAt: string, intervalLabel: string): Promise<WarrantyReminder> {
  const params = new URLSearchParams({ receiptId, intervalLabel, reminderAt });
  const response = await fetch(`/api/reminders?${params.toString()}`, {
    method: "POST",
    headers: await csrfHeaders()
  });
  if (!response.ok) throw new Error("Could not create warranty reminder");
  return await response.json() as WarrantyReminder;
}
