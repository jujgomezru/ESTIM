const BASE_URL = "http://localhost:8080";

export async function apiGet(endpoint) {
  const res = await fetch(BASE_URL + endpoint);
  return res.json(); 
}

export async function apiPost(endpoint, data) {
  const res = await fetch(BASE_URL + endpoint, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });

  return res.json();
}

export async function apiPut(endpoint, data) {
  const res = await fetch(BASE_URL + endpoint, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });

  return res.json();
}

export async function apiDelete(endpoint, data = null) {
  const options = {
    method: "DELETE",
    headers: { "Content-Type": "application/json" },
  };

  // si DELETE necesita body, lo incluimos (ej: eliminar item del carrito)
  if (data) {
    options.body = JSON.stringify(data);
  }

  const res = await fetch(BASE_URL + endpoint, options);
  return res.json();
}

//simpsons
export async function apiSimpsons(url) {
  const res = await fetch(url);
  const data = await res.json();
  return data;
}
