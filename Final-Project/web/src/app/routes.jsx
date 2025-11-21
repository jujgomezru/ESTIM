import { BrowserRouter, Routes, Route } from "react-router-dom";

// COMPONENTS
import Layout from "../components/Layout";

// AUTH
import LoginPage from "../features/auth/LoginPage";
import RegisterPage from "../features/auth/RegisterPage";

// GAMES
import StorePage from "../features/games/StorePage";
import GamesListPage from "../features/games/GamesListPage";
import GameDetailPage from "../features/games/GameDetailPage";

// CART
import CartPage from "../features/cart/CartPage";
import PurchaseSuccessPage from "../features/cart/PurchaseSuccessPage";

// LIBRARY
import LibraryPage from "../features/library/LibraryPage";

// WISHLIST
import WishlistPage from "../features/wishlist/WishlistPage";

//SUPPORT
import HelpCenterPage from '../features/support/HelpCenterPage';
import ContactPage from '../features/support/ContactPage';

// EXTRAS
import SimpsonsPage from "../features/simpsons/SimpsonsPage";


export default function AppRoutes() {
  return (
    <BrowserRouter>
      <Routes>


        {/* AUTH */}
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />

      <Route element={<Layout />}>
        {/* GAMES */}
        <Route path="/" element={<StorePage />} />
        <Route path="/games" element={<GamesListPage />} />
        <Route path="/game/:id" element={<GameDetailPage />} />

        {/* CART */}
        <Route path="/cart" element={<CartPage />} />
        <Route path="/cart/success" element={<PurchaseSuccessPage />} />

        {/* LIBRARY */}
        <Route path="/library" element={<LibraryPage />} />

        {/* WISHLIST */}
        <Route path="/wishlist" element={<WishlistPage />} />

        {/* SUPPORT */}
        <Route path="/help" element={<HelpCenterPage />} />
        <Route path="/contact" element={<ContactPage />} />

        {/* EXTRAS */}
        <Route path="/simpsons" element={<SimpsonsPage />} />
      </Route>

      </Routes>
    </BrowserRouter>
  );
}

