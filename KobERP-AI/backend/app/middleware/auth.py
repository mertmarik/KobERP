from fastapi import HTTPException, Security
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from jose import jwt, JWTError
import requests
from config import settings
from typing import Dict
from functools import lru_cache


security = HTTPBearer()


@lru_cache()
def get_jwks() -> Dict:
    """Fetch and cache Auth0 JWKS (JSON Web Key Set)"""
    jwks_url = f"https://{settings.auth0_domain}/.well-known/jwks.json"
    try:
        response = requests.get(jwks_url, timeout=10)
        response.raise_for_status()
        return response.json()
    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail=f"Unable to fetch JWKS from Auth0: {str(e)}"
        )


def get_rsa_key(token: str) -> Dict:
    """Extract the RSA key from JWKS based on token header"""
    try:
        unverified_header = jwt.get_unverified_header(token)
    except JWTError:
        raise HTTPException(
            status_code=401,
            detail="Invalid token header"
        )
    
    jwks = get_jwks()
    rsa_key = {}
    
    for key in jwks.get("keys", []):
        if key["kid"] == unverified_header["kid"]:
            rsa_key = {
                "kty": key["kty"],
                "kid": key["kid"],
                "use": key["use"],
                "n": key["n"],
                "e": key["e"]
            }
            break
    
    if not rsa_key:
        raise HTTPException(
            status_code=401,
            detail="Unable to find appropriate key"
        )
    
    return rsa_key


def verify_token(credentials: HTTPAuthorizationCredentials = Security(security)) -> Dict:
    """
    Verify Auth0 JWT token and return decoded payload
    
    Args:
        credentials: HTTP Authorization credentials with Bearer token
        
    Returns:
        Dict: Decoded JWT payload
        
    Raises:
        HTTPException: If token is invalid or verification fails
    """
    token = credentials.credentials
    
    try:
        rsa_key = get_rsa_key(token)
        
        # Verify and decode the token (with audience validation only)
        payload = jwt.decode(
            token,
            rsa_key,
            audience=settings.auth0_api_audience,
            options={"verify_iss": False, "verify_signature": True}
        )
        
        return payload
        
    except jwt.ExpiredSignatureError:
        raise HTTPException(
            status_code=401,
            detail="Token has expired"
        )
    except jwt.JWTClaimsError:
        raise HTTPException(
            status_code=401,
            detail="Invalid claims. Please check the audience"
        )
    except JWTError as e:
        raise HTTPException(
            status_code=401,
            detail=f"Unable to validate token: {str(e)}"
        )
    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail=f"Token verification failed: {str(e)}"
        )
