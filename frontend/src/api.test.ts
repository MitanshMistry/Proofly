import { afterEach, describe, expect, it, vi } from "vitest";
import { getAccount, listReceipts } from "./api";

afterEach(() => vi.restoreAllMocks());

describe("Proofly API client", () => {
  it("returns typed receipts from the backend", async () => {
    vi.stubGlobal("fetch", vi.fn().mockResolvedValue({
      ok: true,
      json: async () => [{
        id: "receipt-1",
        merchantName: "Target",
        purchasedAt: "2026-11-18",
        total: 86.4,
        category: "Shopping",
        projectId: null,
        status: "SAVED"
      }]
    }));

    const receipts = await listReceipts();
    expect(receipts[0].merchantName).toBe("Target");
    expect(receipts[0].total).toBe(86.4);
  });

  it("maps an unauthenticated account response", async () => {
    vi.stubGlobal("fetch", vi.fn().mockResolvedValue({ ok: false, status: 401 }));
    await expect(getAccount()).rejects.toThrow("UNAUTHENTICATED");
  });
});
